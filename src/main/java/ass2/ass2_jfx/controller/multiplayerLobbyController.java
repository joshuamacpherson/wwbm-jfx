package ass2.ass2_jfx.controller;

import ass2.ass2_jfx.model.Player;
import ass2.ass2_jfx.model.dataStore;
import ass2.ass2_jfx.model.networkProtocol;
import ass2.ass2_jfx.view.menuBarHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Controller for the Multiplayer Lobby screen.
 * This screen serves two roles depending on whether the user is hosting or joining:
 * - Host: configures port and max players, starts the server, and waits for players to connect
 * - Join: enters a server IP and port, connects as a client, and waits for the host to start
 * The correct UI controls are shown or hidden automatically based on the host/join flag
 * stored in the sceneController. Once the host starts the game, all clients transition
 * to the multiplayer play screen.
 *
 * @author Shane O'Connell
 * @author Joshua MacPherson
 * @version Java 21
 */
public class multiplayerLobbyController {

    /** Label displaying the current lobby status message. */
    @FXML private Label statusLabel;
    /** List view showing chat messages and player join notifications. */
    @FXML private ListView<String> playerListView;
    /** Host-only field for entering the port number to host on. */
    @FXML private TextField portFieldHost;
    /** Host-only spinner for selecting the maximum number of players. */
    @FXML private Spinner<Integer> maxPlayersSpinner;
    /** Host-only button to start the server and begin accepting connections. */
    @FXML private Button startHostingBtn;
    /** Host-only button to start the game once enough players have connected. */
    @FXML private Button startGameBtn;
    /** Join-only field for entering the server IP address. */
    @FXML private TextField ipField;
    /** Join-only field for entering the server port number to connect to. */
    @FXML private TextField portFieldJoin;
    /** Join-only button to initiate a connection to the server. */
    @FXML private Button connectBtn;
    /** The game server instance, only non-null when this player is the host. */
    private gameServer server;
    /** The game client used to communicate with the server. */
    private gameClient client;
    /** The name of the currently selected player. */
    private String playerName;

    /**
     * Initializes the lobby screen.
     * Prompts the user to select a player, then shows either host or join controls
     * depending on whether this client is the host.
     */
    @FXML
    private void initialize() {
        boolean isHost = sceneController.getInstance().isHost();

        ArrayList<Player> players =
                dataStore.getInstance().getPlayers();
        if (players.isEmpty()) {
            statusLabel.setText("No players. Create one in Design.");
            disableAll();
            return;
        }

        ChoiceDialog<Player> dialog =
                new ChoiceDialog<>(players.get(0), players);
        dialog.setTitle("Select Player");
        dialog.setHeaderText("Choose your player");
        dialog.setContentText("Player:");
        Optional<Player> result = dialog.showAndWait();

        if (result.isEmpty()) {
            statusLabel.setText("No player selected.");
            disableAll();
            return;
        }

        Player selected = result.get();
        dataStore.getInstance().setCurrentPlayer(selected);
        playerName = selected.getName();

        if (isHost) {
            showHostUI(true);
            showJoinUI(false);
            maxPlayersSpinner.setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(
                            2, 8, 2));
            startGameBtn.setDisable(true);
            statusLabel.setText("Configure and start hosting.");
        } else {
            showHostUI(false);
            showJoinUI(true);
            statusLabel.setText("Enter server IP and port to connect.");
        }
    }

    /**
     * Handles the Start Hosting button click.
     * Creates and starts a game server on the specified port, then connects
     * the host as a client to their own server.
     */
    @FXML
    private void onStartHosting() {
        try {
            int port = Integer.parseInt(
                    portFieldHost.getText().trim());
            int maxPlayers = maxPlayersSpinner.getValue();

            server = new gameServer(port, maxPlayers);
            server.setOnPlayerJoined(() -> {
                playerListView.getItems().clear();
                statusLabel.setText(
                        server.getConnectedCount()
                                + "/" + server.getMaxPlayers()
                                + " players connected");
                if (server.getConnectedCount()
                        >= server.getMaxPlayers()) {
                    startGameBtn.setDisable(false);
                }
            });

            server.start();
            startHostingBtn.setDisable(true);
            statusLabel.setText(
                    "Hosting on port " + port
                            + ". Waiting for players...");

            client = new gameClient();
            client.setOnMessageReceived(this::handleLobbyMessage);
            client.connect("localhost", port, playerName);

        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid port number.");
        } catch (IOException e) {
            statusLabel.setText("Failed to start: " + e.getMessage());
        }
    }

    /**
     * Handles the Connect button click.
     * Creates a client and attempts to connect to the server at the specified IP and port.
     */
    @FXML
    private void onConnect() {
        try {
            String ip = ipField.getText().trim();
            int port = Integer.parseInt(
                    portFieldJoin.getText().trim());

            client = new gameClient();
            client.setOnMessageReceived(this::handleLobbyMessage);
            client.connect(ip, port, playerName);

            connectBtn.setDisable(true);
            statusLabel.setText("Connected! Waiting for host to start...");

        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid port number.");
        } catch (IOException e) {
            statusLabel.setText("Connection failed: " + e.getMessage());
        }
    }

    /**
     * Handles the Start Game button click.
     * Only available to the host. Tells the server to begin the game.
     * @param event the button click event
     */
    @FXML
    private void onStartGame(ActionEvent event) {
        if (server != null) {
            server.startGame();
        }
    }

    /**
     * Handles incoming messages from the server while in the lobby.
     * Reacts to START by transitioning to the play screen, CHAT by displaying
     * messages in the player list, and ERROR by showing the error in the status label.
     * @param message the raw message string received from the server
     */
    private void handleLobbyMessage(String message) {
        String[] parts = networkProtocol.parse(message);
        if (parts.length == 0) return;

        switch (parts[0]) {
            case networkProtocol.START -> {
                try {
                    multiplayerState.getInstance().setClient(client);
                    multiplayerState.getInstance().setServer(server);
                    multiplayerState.getInstance().setPlayerName(playerName);
                    sceneController.getInstance().switchToMultiplayerPlay();
                } catch (IOException e) {
                    statusLabel.setText("Failed to load game screen.");
                }
            }

            case networkProtocol.CHAT -> {
                if (parts.length >= 3) {
                    playerListView.getItems().add(parts[1] + ": " + parts[2]);
                }
            }

            case networkProtocol.ERROR -> {
                if (parts.length >= 2) {
                    statusLabel.setText("Error: " + parts[1]);
                }
            }
        }
    }

    /**
     * Handles the Back button click.
     * Disconnects the client and stops the server if hosting, then returns to the main menu.
     * @param event the button click event
     */
    @FXML
    private void onBackClick(ActionEvent event) {
        if (client != null) client.disconnect(playerName);
        if (server != null) server.stop();
        try {
            sceneController.getInstance().switchToMenu(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows or hides the host-only UI controls.
     * @param visible true to show host controls, false to hide them
     */
    private void showHostUI(boolean visible) {
        portFieldHost.setVisible(visible);
        portFieldHost.setManaged(visible);
        maxPlayersSpinner.setVisible(visible);
        maxPlayersSpinner.setManaged(visible);
        startHostingBtn.setVisible(visible);
        startHostingBtn.setManaged(visible);
        startGameBtn.setVisible(visible);
        startGameBtn.setManaged(visible);
    }

    /**
     * Shows or hides the join-only UI controls.
     * @param visible true to show join controls, false to hide them
     */
    private void showJoinUI(boolean visible) {
        ipField.setVisible(visible);
        ipField.setManaged(visible);
        portFieldJoin.setVisible(visible);
        portFieldJoin.setManaged(visible);
        connectBtn.setVisible(visible);
        connectBtn.setManaged(visible);
    }

    /**
     * Hides all host and join UI controls.
     * Used when no players exist or no player is selected.
     */
    private void disableAll() {
        showHostUI(false);
        showJoinUI(false);
    }

    /** Exits the application. */
    @FXML private void onExitClick() { menuBarHelper.exit(); }

    /** Applies the dark theme. */
    @FXML private void onDarkClick() { menuBarHelper.setDark(); }

    /** Applies the light theme. */
    @FXML private void onLightClick() { menuBarHelper.setLight(); }
}