package ass2.ass2_jfx.controller;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Represents the client-side network connection for multiplayer gameplay.
 *   This class is responsible for:
 *   Connecting to a game server via TCP socket
 *   Sending messages to the server (connect, answer, chat, disconnect)
 *   Listening for incoming messages from the server on a background thread
 *   Forwarding received messages to the UI thread via a callback
 *   All incoming messages are dispatched to the JavaFX Application Thread
 *
 * @author Shane O'Connell
 * @author Joshua MacPherson
 * @version Java 21
 */
public class gameClient {

    /** The TCP socket connection to the game server. */
    private Socket socket;

    /** Writer used to send messages to the server. */
    private PrintWriter out;

    /** Reader used to receive messages from the server. */
    private BufferedReader in;

    /** Callback invoked on the JavaFX thread when a message is received. */
    private Consumer<String> onMessageReceived;

    /** Whether the client listener thread should continue running. */
    private volatile boolean running = false;

    /**
     * Sets the callback to be invoked when a message is received from the server.
     * The callback is always called on the JavaFX Application Thread.
     * @param callback the message handler to invoke with each received message
     */
    public void setOnMessageReceived(Consumer<String> callback) {
        this.onMessageReceived = callback;
    }

    /**
     * Connects to the game server at the specified host and port.
     * Sends an initial {@code CONNECT} message with the player's name,
     * then starts a background listener thread that reads incoming messages
     * and forwards them to the registered callback on the JavaFX thread.
     *
     * @param host       the server hostname or IP address
     * @param port       the server port number
     * @param playerName the name of the player connecting
     * @throws IOException if the connection cannot be established
     */
    public void connect(String host, int port, String playerName)
            throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        running = true;

        send(networkProtocol.build(
                networkProtocol.CONNECT, playerName
        ));

        new Thread(() -> {
            try {
                String line;
                while (running && (line = in.readLine()) != null) {
                    final String msg = line;
                    if (onMessageReceived != null) {
                        javafx.application.Platform
                                .runLater(() -> onMessageReceived
                                        .accept(msg));
                    }
                }
            } catch (IOException e) {
                if (running) {
                    System.out.println(
                            "Disconnected from server.");
                }
            }
        }, "ClientListener").start();
    }

    /**
     * Sends a raw message string to the server.
     * @param message the message to send
     */
    public void send(String message) {
        if (out != null) out.println(message);
    }

    /**
     * Sends the player's answer to the server.
     * @param playerId    the name or ID of the player submitting the answer
     * @param answerIndex the index (0-3) of the selected answer
     */
    public void sendAnswer(String playerId, int answerIndex) {
        send(networkProtocol.build(
                networkProtocol.ANSWER,
                playerId,
                String.valueOf(answerIndex)
        ));
    }

    /**
     * Sends a chat message to the server to be broadcast to all players.
     * @param playerName the name of the player sending the message
     * @param message    the chat message text
     */
    public void sendChat(String playerName, String message) {
        send(networkProtocol.build(
                networkProtocol.CHAT, playerName, message
        ));
    }

    /**
     * Disconnects from the server by sending a DISCONNECT message
     * and closing the socket.
     * @param playerName the name of the player disconnecting
     */
    public void disconnect(String playerName) {
        running = false;
        send(networkProtocol.build(
                networkProtocol.DISCONNECT, playerName
        ));
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns whether the client is currently connected to the server.
     * @return {@code true} if the socket is open and connected; {@code false} otherwise
     */
    public boolean isConnected() {
        return socket != null
                && socket.isConnected()
                && !socket.isClosed();
    }
}