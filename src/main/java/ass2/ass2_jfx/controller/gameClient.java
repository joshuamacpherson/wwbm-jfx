package ass2.ass2_jfx.controller;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class gameClient {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Consumer<String> onMessageReceived;
    private volatile boolean running = false;

    public void setOnMessageReceived(Consumer<String> callback) {
        this.onMessageReceived = callback;
    }

    public void connect(String host, int port, String playerName)
            throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        running = true;

        // send CONNECT message
        send(networkProtocol.build(
                networkProtocol.CONNECT, playerName
        ));

        // listener thread
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

    public void send(String message) {
        if (out != null) out.println(message);
    }

    public void sendAnswer(String playerId, int answerIndex) {
        send(networkProtocol.build(
                networkProtocol.ANSWER,
                playerId,
                String.valueOf(answerIndex)
        ));
    }

    public void sendChat(String playerName, String message) {
        send(networkProtocol.build(
                networkProtocol.CHAT, playerName, message
        ));
    }

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

    public boolean isConnected() {
        return socket != null
                && socket.isConnected()
                && !socket.isClosed();
    }
}