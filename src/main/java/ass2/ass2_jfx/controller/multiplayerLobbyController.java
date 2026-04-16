package ass2.ass2_jfx.controller;

import ass2.ass2_jfx.model.Player;
import ass2.ass2_jfx.model.dataStore;
import ass2.ass2_jfx.view.menuBarHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class multiplayerLobbyController {

    // shared UI
    @FXML private Label statusLabel;
    @FXML private ListView<String> playerListView;

    // host-only UI
    @FXML private TextField portFieldHost;
    @FXML private Spinner<Integer> maxPlayersSpinner;
    @FXML private Button startHostingBtn;
    @FXML private Button startGameBtn;

    // join-only UI
    @FXML private TextField ipField;
    @FXML private TextField portFieldJoin;
    @FXML private Button connectBtn;

    private gameServer server;
    private gameClient client;
    private String playerName;

    @FXML
    private void initialize() {
        boolean isHost = sceneController.getInstance().isHost();

        // pick player first
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
            // show host controls, hide join controls
            showHostUI(true);
            showJoinUI(false);

            maxPlayersSpinner.setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(
                            2, 8, 2));
            startGameBtn.setDisable(true);
            statusLabel.setText(
                    "Configure and start hosting.");
        } else {
            // show join controls, hide host controls
            showHostUI(false);
            showJoinUI(true);
            statusLabel.setText(
                    "Enter server IP and port to connect.");
        }
    }

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

            // host joins own server as client
            client = new gameClient();
            client.setOnMessageReceived(
                    this::handleLobbyMessage);
            client.connect("localhost", port, playerName);

        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid port number.");
        } catch (IOException e) {
            statusLabel.setText(
                    "Failed to start: " + e.getMessage());
        }
    }

    @FXML
    private void onConnect() {
        try {
            String ip = ipField.getText().trim();
            int port = Integer.parseInt(
                    portFieldJoin.getText().trim());

            client = new gameClient();
            client.setOnMessageReceived(
                    this::handleLobbyMessage);
            client.connect(ip, port, playerName);

            connectBtn.setDisable(true);
            statusLabel.setText(
                    "Connected! Waiting for host to start...");

        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid port number.");
        } catch (IOException e) {
            statusLabel.setText(
                    "Connection failed: " + e.getMessage());
        }
    }

    @FXML
    private void onStartGame(ActionEvent event) {
        if (server != null) {
            server.startGame();
        }
    }

    private void handleLobbyMessage(String message) {
        String[] parts = networkProtocol.parse(message);
        if (parts.length == 0) return;

        switch (parts[0]) {
            case networkProtocol.START -> {
                // transition to multiplayer play screen
                try {
                    // store client/server refs for play screen
                    multiplayerState.getInstance()
                            .setClient(client);
                    multiplayerState.getInstance()
                            .setServer(server);
                    multiplayerState.getInstance()
                            .setPlayerName(playerName);

                    sceneController.getInstance()
                            .switchToMultiplayerPlay();
                } catch (IOException e) {
                    statusLabel.setText(
                            "Failed to load game screen.");
                }
            }

            case networkProtocol.CHAT -> {
                if (parts.length >= 3) {
                    playerListView.getItems()
                            .add(parts[1] + ": " + parts[2]);
                }
            }

            case networkProtocol.ERROR -> {
                if (parts.length >= 2) {
                    statusLabel.setText("Error: " + parts[1]);
                }
            }
        }
    }

    @FXML
    private void onBackClick(ActionEvent event) {
        if (client != null) client.disconnect(playerName);
        if (server != null) server.stop();
        try {
            sceneController.getInstance()
                    .switchToMenu(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private void showJoinUI(boolean visible) {
        ipField.setVisible(visible);
        ipField.setManaged(visible);
        portFieldJoin.setVisible(visible);
        portFieldJoin.setManaged(visible);
        connectBtn.setVisible(visible);
        connectBtn.setManaged(visible);
    }

    private void disableAll() {
        showHostUI(false);
        showJoinUI(false);
    }

    @FXML private void onExitClick() { menuBarHelper.exit(); }
    @FXML private void onDarkClick() { menuBarHelper.setDark(); }
    @FXML private void onLightClick() { menuBarHelper.setLight(); }
}