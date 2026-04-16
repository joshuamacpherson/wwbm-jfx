package ass2.ass2_jfx.controller;

import ass2.ass2_jfx.model.Question;
import ass2.ass2_jfx.model.dataStore;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class gameServer {

    private ServerSocket serverSocket;
    private final int port;
    private final int maxPlayers;
    private final List<ClientHandler> clients =
            Collections.synchronizedList(new ArrayList<>());
    private final ArrayList<Question> questions =
            dataStore.getInstance().getQuestions();
    private int currentQuestionIndex = 0;
    private final Map<String, String> playerAnswers =
            new ConcurrentHashMap<>();
    private volatile boolean running = false;

    // callback so lobby UI can react to connections
    private Runnable onPlayerJoined;

    public gameServer(int port, int maxPlayers) {
        this.port = port;
        this.maxPlayers = maxPlayers;
    }

    public void setOnPlayerJoined(Runnable callback) {
        this.onPlayerJoined = callback;
    }

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

    public void startGame() {
        currentQuestionIndex = 0;
        broadcast(networkProtocol.build(
                networkProtocol.START,
                String.valueOf(clients.size())
        ));
        sendCurrentQuestion();
    }

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
                answers[0], answers[1], answers[2], answers[3]
        ));
        playerAnswers.clear();
    }

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

                // advance when all players answered
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

    public void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.send(message);
            }
        }
    }

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

    public int getConnectedCount() {
        return clients.size();
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    private int getPrizeForTier(int tier) {
        int[] prices = {
                100, 200, 300, 500, 1000,
                2000, 4000, 8000, 16000, 32000,
                64000, 125000, 250000, 500000, 1000000
        };
        return tier < prices.length ? prices[tier] : 0;
    }

    // Inner class: one per connected client
    public static class ClientHandler implements Runnable {
        private final Socket socket;
        private final gameServer server;
        private PrintWriter out;
        private BufferedReader in;
        private String playerName = "Unknown";

        public ClientHandler(Socket socket, gameServer server) {
            this.socket = socket;
            this.server = server;
        }

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

        public void send(String message) {
            if (out != null) out.println(message);
        }

        public void close() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String name) {
            this.playerName = name;
        }
    }
}