package ass2.ass2_jfx.controller;

import ass2.ass2_jfx.model.Question;
import ass2.ass2_jfx.model.dataStore;
import ass2.ass2_jfx.model.networkProtocol;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Represents the server-side network host for multiplayer gameplay.
 * This class is responsible for:
 * - Accepting incoming client connections up to a configured maximum
 * - Broadcasting questions to all connected players
 * - Receiving and evaluating player answers
 * - Tracking scores and advancing through the question list
 * - Broadcasting chat messages between players
 * - Cleanly shutting down all connections when the game ends
 * Each connected client is managed by an inner {@link ClientHandler} instance
 * running on its own thread. The server runs on a dedicated background thread
 * and notifies the lobby UI via a callback when a player joins.
 *
 * @author Shane O'Connell
 * @author Joshua MacPherson
 * @version Java 21
 */
public class gameServer {

    /** The server socket that listens for incoming client connections. */
    private ServerSocket serverSocket;
    /** The port number the server listens on. */
    private final int port;
    /** The maximum number of players allowed in the game. */
    private final int maxPlayers;
    /** Thread-safe list of all currently connected client handlers. */
    private final List<ClientHandler> clients =
            Collections.synchronizedList(new ArrayList<>());
    /** The full list of questions loaded from the data store. */
    private final ArrayList<Question> questions =
            dataStore.getInstance().getQuestions();
    /** Index of the question currently being asked. */
    private int currentQuestionIndex = 0;
    /** Maps player IDs to their submitted answer for the current question. */
    private final Map<String, String> playerAnswers =
            new ConcurrentHashMap<>();
    /** Whether the server is currently running and accepting connections. */
    private volatile boolean running = false;
    /** Optional callback invoked on the JavaFX thread when a player joins. */
    private Runnable onPlayerJoined;

    /**
     * Constructs a new game server with the specified port and player limit.
     * @param port       the port number to listen on
     * @param maxPlayers the maximum number of players allowed to connect
     */
    public gameServer(int port, int maxPlayers) {
        this.port = port;
        this.maxPlayers = maxPlayers;
    }

    /**
     * Sets a callback to be invoked on the JavaFX thread each time a player joins.
     * Used by the lobby UI to refresh the player count display.
     * @param callback the runnable to invoke when a player connects
     */
    public void setOnPlayerJoined(Runnable callback) {
        this.onPlayerJoined = callback;
    }

    /**
     * Starts the server on a background thread.
     * Accepts incoming connections until the maximum player count is reached
     * or the server is stopped. Each accepted connection is handed off to
     * a new {@link ClientHandler} thread.
     */
    public void start() {
        running = true;
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Server started on port " + port);

                while (running && clients.size() < maxPlayers) {
                    Socket socket = serverSocket.accept();
                    ClientHandler handler =
                            new ClientHandler(socket, this);
                    clients.add(handler);
                    new Thread(handler).start();

                    if (onPlayerJoined != null) {
                        javafx.application.Platform
                                .runLater(onPlayerJoined);
                    }
                }
            } catch (IOException e) {
                if (running) e.printStackTrace();
            }
        }, "ServerThread").start();
    }

    /**
     * Starts the game by resetting the question index, broadcasting a START
     * message to all clients, and sending the first question.
     */
    public void startGame() {
        currentQuestionIndex = 0;
        broadcast(networkProtocol.build(
                networkProtocol.START,
                String.valueOf(clients.size())
        ));
        sendCurrentQuestion();
    }

    /**
     * Broadcasts the current question to all connected clients.
     * If all questions have been exhausted, broadcasts a GAMEOVER result instead.
     * Clears the player answer map in preparation for new responses.
     */
    public void sendCurrentQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            broadcast(networkProtocol.build(
                    networkProtocol.RESULT, "ALL", "GAMEOVER", "0"
            ));
            return;
        }

        Question q = questions.get(currentQuestionIndex);
        String[] answers = q.getAnswers();
        broadcast(networkProtocol.build(
                networkProtocol.QUESTION,
                q.getQuestionText(),
                answers[0], answers[1], answers[2], answers[3],
                String.valueOf(q.getCorrectIndex())
        ));
        playerAnswers.clear();
    }

    /**
     * Handles an incoming message from a connected client.
     * Routes the message based on its type: CONNECT, ANSWER, CHAT, or DISCONNECT.
     * When all players have answered, advances to the next question automatically.
     * @param message the raw message string received from the client
     * @param sender  the ClientHandler that sent the message
     */
    public void handleMessage(String message, ClientHandler sender) {
        String[] parts = networkProtocol.parse(message);
        if (parts.length == 0) return;

        switch (parts[0]) {
            case networkProtocol.CONNECT -> {
                sender.setPlayerName(parts[1]);
                broadcast(networkProtocol.build(
                        networkProtocol.CHAT,
                        "SERVER",
                        parts[1] + " has joined!"
                ));
            }

            case networkProtocol.ANSWER -> {
                String playerId = parts[1];
                int answerIndex = Integer.parseInt(parts[2]);
                playerAnswers.put(playerId, parts[2]);

                Question q = questions.get(currentQuestionIndex);
                boolean correct = q.isCorrect(answerIndex);
                int money = correct
                        ? getPrizeForTier(currentQuestionIndex)
                        : 0;

                broadcast(networkProtocol.build(
                        networkProtocol.RESULT,
                        playerId,
                        String.valueOf(correct),
                        String.valueOf(money)
                ));

                if (playerAnswers.size() >= clients.size()) {
                    currentQuestionIndex++;
                    sendCurrentQuestion();
                }
            }

            case networkProtocol.CHAT -> {
                broadcast(networkProtocol.build(
                        networkProtocol.CHAT,
                        sender.getPlayerName(),
                        parts.length > 2 ? parts[2] : ""
                ));
            }

            case networkProtocol.DISCONNECT -> {
                clients.remove(sender);
                broadcast(networkProtocol.build(
                        networkProtocol.CHAT,
                        "SERVER",
                        sender.getPlayerName() + " disconnected."
                ));
            }
        }
    }

    /**
     * Broadcasts a message to all currently connected clients.
     * @param message the message to send to every client
     */
    public void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.send(message);
            }
        }
    }

    /**
     * Stops the server by closing the server socket and disconnecting all clients.
     * Sends a DISCONNECT message to each client before closing their connections.
     */
    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        synchronized (clients) {
            for (ClientHandler c : clients) {
                c.send(networkProtocol.build(
                        networkProtocol.DISCONNECT, "SERVER"
                ));
                c.close();
            }
            clients.clear();
        }
    }

    /**
     * Returns the number of players currently connected to the server.
     * @return the connected player count
     */
    public int getConnectedCount() {
        return clients.size();
    }

    /**
     * Returns the maximum number of players allowed in this game session.
     * @return the maximum player count
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Returns the prize money awarded for correctly answering a question at the given tier.
     * @param tier the zero-based question index
     * @return the prize amount in dollars, or 0 if the tier is out of range
     */
    private int getPrizeForTier(int tier) {
        int[] prices = {
                100, 200, 300, 500, 1000,
                2000, 4000, 8000, 16000, 32000,
                64000, 125000, 250000, 500000, 1000000
        };
        return tier < prices.length ? prices[tier] : 0;
    }

    /**
     * Handles communication with a single connected client on a dedicated thread.
     * Each ClientHandler reads incoming messages from its socket and forwards
     * them to the server for processing. It also provides a send method for
     * writing outgoing messages back to the client.
     */
    public static class ClientHandler implements Runnable {
        /** The socket connection to this client. */
        private final Socket socket;
        /** Reference to the parent server for message handling. */
        private final gameServer server;
        /** Writer used to send messages to this client. */
        private PrintWriter out;
        /** Reader used to receive messages from this client. */
        private BufferedReader in;
        /** The display name of this player, set on CONNECT. */
        private String playerName = "Unknown";

        /**
         * Constructs a new ClientHandler for the given socket and server.
         * @param socket the client's socket connection
         * @param server the game server managing this client
         */
        public ClientHandler(Socket socket, gameServer server) {
            this.socket = socket;
            this.server = server;
        }

        /**
         * Listens for incoming messages from the client and forwards each one
         * to the server's message handler. Removes itself from the server's
         * client list and closes the socket when the connection ends.
         */
        @Override
        public void run() {
            try {
                in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
                out = new PrintWriter(
                        socket.getOutputStream(), true);

                String line;
                while ((line = in.readLine()) != null) {
                    server.handleMessage(line, this);
                }
            } catch (IOException e) {
                System.out.println(
                        playerName + " disconnected.");
            } finally {
                server.clients.remove(this);
                close();
            }
        }

        /**
         * Sends a message to this client.
         * @param message the message to send
         */
        public void send(String message) {
            if (out != null) out.println(message);
        }

        /**
         * Closes this client's socket connection.
         */
        public void close() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Returns the display name of this player.
         * @return the player's name
         */
        public String getPlayerName() {
            return playerName;
        }

        /**
         * Sets the display name of this player.
         * @param name the player's name
         */
        public void setPlayerName(String name) {
            this.playerName = name;
        }
    }
}