package ass2.ass2_jfx.controller;

/**
 * Shared state container for an active multiplayer session.
 * Implements the Singleton pattern to provide a single source of truth
 * for the client, server, and player name across the lobby and play screens.
 * The server field is only set when this player is the host. If the player
 * joined someone else's game, server will be null.
 * @author Shane O'Connell
 * @author Joshua MacPherson
 * @version Java 21
 */
public class multiplayerState {
    /** Singleton instance of multiplayerState. */
    private static multiplayerState instance;
    /** The game client used to communicate with the server. */
    private gameClient client;
    /** The game server instance, only non-null when this player is the host. */
    private gameServer server;
    /** The name of the current player in this session. */
    private String playerName;
    /** Private constructor to prevent external instantiation. */
    private multiplayerState() {}

    /**
     * Returns the singleton instance of multiplayerState.
     * @return the shared multiplayerState instance
     */
    public static multiplayerState getInstance() {
        if (instance == null) {
            instance = new multiplayerState();
        }
        return instance;
    }

    /**
     * Returns the game client for this session.
     * @return the active gameClient
     */
    public gameClient getClient() { return client; }

    /**
     * Sets the game client for this session.
     * @param client the gameClient to use
     */
    public void setClient(gameClient client) {
        this.client = client;
    }

    /**
     * Returns the game server for this session, or null if this player is not the host.
     * @return the active gameServer, or null if joining
     */
    public gameServer getServer() { return server; }

    /**
     * Sets the game server for this session.
     * Should only be set when this player is hosting.
     * @param server the gameServer instance to store
     */
    public void setServer(gameServer server) {
        this.server = server;
    }

    /**
     * Returns the name of the current player.
     * @return the player's name
     */
    public String getPlayerName() { return playerName; }

    /**
     * Sets the name of the current player.
     * @param name the player's name
     */
    public void setPlayerName(String name) {
        this.playerName = name;
    }

    /**
     * Returns whether this player is the host of the current session.
     * A player is considered the host if a server instance has been set.
     * @return true if this player is hosting, false if joining
     */
    public boolean isHost() { return server != null; }
}