package ass2.ass2_jfx.controller;

public class multiplayerState {

    private static multiplayerState instance;
    private gameClient client;
    private gameServer server; // null if not host
    private String playerName;

    private multiplayerState() {}

    public static multiplayerState getInstance() {
        if (instance == null) {
            instance = new multiplayerState();
        }
        return instance;
    }

    public gameClient getClient() { return client; }
    public void setClient(gameClient client) {
        this.client = client;
    }

    public gameServer getServer() { return server; }
    public void setServer(gameServer server) {
        this.server = server;
    }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public boolean isHost() { return server != null; }
}