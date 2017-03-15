package controller;

import bot.Bot;
import bot.ErrorBot;
import bot.StatusBot;
import cfg.CFGData;
import cfg.CFGManager;
import irc.Client;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Pair;

import java.util.Optional;

public class ControllerUI implements IControllerBot {

    @FXML // fx:id="statusLabel"
    public Label statusLabel; // Value injected by FXMLLoader

    @FXML // fx:id="commandTable"
    private TableView<CFGData> commandTable; // Value injected by FXMLLoader

    @FXML // fx:id="commandColumn"
    private TableColumn<CFGData, String> commandColumn; // Value injected by FXMLLoader

    @FXML // fx:id="messageColumn"
    private TableColumn<CFGData, String> messageColumn; // Value injected by FXMLLoader

    private Bot bot;

    private CFGManager cfgManager;

    private HostServices hostServices;

    @FXML
    // This method is called by the FXMLLoader when initialization is complete
    public void initialize() {
        assert commandTable != null : "fx:id=\"commandTable\" was not injected: check your FXML file 'ui.fxml'.";
        assert commandColumn != null : "fx:id=\"commandColumn\" was not injected: check your FXML file 'ui.fxml'.";
        assert messageColumn != null : "fx:id=\"messageColumn\" was not injected: check your FXML file 'ui.fxml'.";

        cfgManager = new CFGManager();
        loadTable();
    }

    @FXML
    public void startBot() {
        bot = new Bot(new Client(), this);
        bot.start();
        statusBot(StatusBot.RUNNING);
    }

    @FXML
    public void stopBot() {
        if (bot != null) {
            bot.stopp();
        }
        statusBot(StatusBot.STOPPED);
    }

    @FXML
    public void settingsBot() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Bot Settings");

        // Set the button types.
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(90.0);
        anchorPane.setPrefWidth(350.0);

        Label lblUser = new Label();
        lblUser.setLayoutX(10.0);
        lblUser.setLayoutY(10.0);
        lblUser.setText("Twitch Username:");
        anchorPane.getChildren().add(lblUser);

        Label lblPass = new Label();
        lblPass.setLayoutX(10.0);
        lblPass.setLayoutY(42.0);
        lblPass.setText("Twitch OAuth Token:");
        anchorPane.getChildren().add(lblPass);

        TextField user = new TextField();
        user.setPrefHeight(25.0);
        user.setPrefWidth(266.0);
        user.setLayoutX(130.0);
        user.setLayoutY(6.0);
        user.setPromptText(cfgManager.getNick());
        anchorPane.getChildren().add(user);

        TextField pass = new TextField();
        pass.setPrefHeight(25.0);
        pass.setPrefWidth(266.0);
        pass.setLayoutX(130.0);
        pass.setLayoutY(37.0);
        pass.setPromptText(cfgManager.getPass());
        anchorPane.getChildren().add(pass);

        Hyperlink twitchOAuthLink = new Hyperlink("Twitch OAuth Token Generator");
        twitchOAuthLink.setLayoutX(6.0);
        twitchOAuthLink.setLayoutY(70.0);
        twitchOAuthLink.setOnAction(event -> hostServices.showDocument("https://twitchapps.com/tmi/"));
        anchorPane.getChildren().add(twitchOAuthLink);

        dialog.getDialogPane().setContent(anchorPane);

        // Convert the result when the save button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) { // salva i nuovi dati nel json e carica i nuovi dati nella tabella
                return new Pair<>(user.getText(), pass.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(consumer -> {
            System.out.println("User=" + consumer.getKey() + ", Pass=" + consumer.getValue());
            String uuser = consumer.getKey();
            if (uuser.length() > 0) cfgManager.setNick(uuser);
            String ppass = consumer.getValue();
            if (ppass.length() > 0) cfgManager.setPass(ppass);
        });

    }

    @FXML
    public void addCommand() {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add Command");

        // Set the button types.
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(90.0);
        anchorPane.setPrefWidth(350.0);

        Label lblCmd = new Label();
        lblCmd.setLayoutX(10.0);
        lblCmd.setLayoutY(10.0);
        lblCmd.setText("Command:");
        anchorPane.getChildren().add(lblCmd);

        Label lblMsg = new Label();
        lblMsg.setLayoutX(10.0);
        lblMsg.setLayoutY(42.0);
        lblMsg.setText("Message:");
        anchorPane.getChildren().add(lblMsg);

        TextField command = new TextField();
        command.setPrefHeight(25.0);
        command.setPrefWidth(266.0);
        command.setLayoutX(80.0);
        command.setLayoutY(6.0);
        anchorPane.getChildren().add(command);

        TextField message = new TextField();
        message.setPrefHeight(25.0);
        message.setPrefWidth(266.0);
        message.setLayoutX(80.0);
        message.setLayoutY(37.0);
        anchorPane.getChildren().add(message);

        dialog.getDialogPane().setContent(anchorPane);

        // Convert the result when the save button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) { // salva i nuovi dati nel json e carica i nuovi dati nella tabella
                return new Pair<>(command.getText(), message.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(consumer -> {
            System.out.println("Command=" + consumer.getKey() + ", Message=" + consumer.getValue());
            cfgManager.addCommand(consumer.getKey(), consumer.getValue());
            loadTable();
        });
    }

    @FXML
    public void editCommand() {
        CFGData selected = commandTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No command selected!");

            alert.showAndWait();
            return;
        }

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Command");

        // Set the button types.
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(90.0);
        anchorPane.setPrefWidth(350.0);

        Label lblCmd = new Label();
        lblCmd.setLayoutX(10.0);
        lblCmd.setLayoutY(10.0);
        lblCmd.setText("Command:");
        anchorPane.getChildren().add(lblCmd);

        Label lblMsg = new Label();
        lblMsg.setLayoutX(10.0);
        lblMsg.setLayoutY(42.0);
        lblMsg.setText("Message:");
        anchorPane.getChildren().add(lblMsg);

        TextField command = new TextField();
        command.setPrefHeight(25.0);
        command.setPrefWidth(266.0);
        command.setLayoutX(80.0);
        command.setLayoutY(6.0);
        command.setText(selected.getCmd());
        anchorPane.getChildren().add(command);

        TextField message = new TextField();
        message.setPrefHeight(25.0);
        message.setPrefWidth(266.0);
        message.setLayoutX(80.0);
        message.setLayoutY(37.0);
        message.setText(selected.getMsg());
        anchorPane.getChildren().add(message);

        dialog.getDialogPane().setContent(anchorPane);

        // Convert the result when the save button is clicked.
        dialog.setResultConverter(buttonTypeClicked -> {
            if (buttonTypeClicked == saveButtonType) return new Pair<>(command.getText(), message.getText());
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(consumer -> {
            cfgManager.editCommand(selected, consumer.getKey(), consumer.getValue());
            loadTable();
        });
    }

    @FXML
    public void deleteCommand() {
        CFGData selected = commandTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No command selected!");

            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Command");
        alert.setHeaderText("Delete the command");

        alert.setContentText("Command: " + selected.getCmd() + "\r\nMessage: " + selected.getMsg());

        Optional<ButtonType> result = alert.showAndWait();

        result.ifPresent(consumer -> {
            if (result.get() == ButtonType.OK) {
                cfgManager.deleteCommand(selected);
                loadTable();
            }
        });
    }

    @FXML
    public void gettingStarted() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Getting Started");
        alert.setHeaderText("FUNCTIONALITY AND USE CASES");

        TextArea textArea = new TextArea();
        textArea.setText(
                "BOT SETTINGS\n\n" +
                        "- START BOT:\n  Menu Bar -> Bot -> Start\n\n" +
                        "- STOP BOT:\n  Menu Bat -> Bot -> Stop\n\n" +
                        "- SETTINGS BOT:\n  Menu Bar -> Bot -> Settings\n\n\n" +

                        "BOT COMMANDS\n\n" +
                        "- ADD COMMAND:\n  Menu Bar -> Command -> Add\n\n" +
                        "- EDIT COMMAND:\n  Select the command from the table -> Manu Bar -> Command -> Edit\n\n" +
                        "- DELETE COMMAND:\n  Select the command from the table -> Manu Bar -> Command -> Delete"
        );
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane content = new GridPane();
        content.setMaxWidth(Double.MAX_VALUE);
        content.add(textArea, 0, 0);

        alert.getDialogPane().setContent(content);

        alert.showAndWait();
    }

    @FXML
    public void about() {
/*      Hyperlink github = new Hyperlink("ST3NO on Github");
        github.setOnAction(event -> hostServices.showDocument("https://github.com/st3no"));

        DialogPane dialog = new DialogPane();
        dialog.setHeaderText("AstroBOT");
        dialog.setGraphic(new Alert(Alert.AlertType.INFORMATION).getGraphic());
        dialog.setContentText("Developed by ST3NO");
        dialog.setExpandableContent(github);

        dialog.getButtonTypes().addAll(new ButtonType(ButtonType.OK.getText()));
*/

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("AstroBOT");
        alert.setContentText("Developed by ST3NO - Follow on Github");
        alert.show();
    }

    public void errorBot(ErrorBot error) {
        String errorMessage;

        switch (error) {
            case NICK:
                errorMessage = "Invalid Username";
                break;
            case PASS:
                errorMessage = "Improperly formatted OAuth";
                break;
            default:
                errorMessage = "ERROR";
                break;
        }

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(errorMessage);

            ButtonType settingsButtonType = new ButtonType("Settings", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().add(settingsButtonType);

            alert.showAndWait().ifPresent(buttonTypeClicked -> {
                if (buttonTypeClicked == settingsButtonType) settingsBot();
            });
        });
    }

    public void statusBot(StatusBot status) {
        String statusStr = "";

        switch (status) {
            case RUNNING:
                statusStr += "Running";
                break;
            case STOPPED:
                statusStr += "Stopped";
                break;
        }

        String statusMessage = "Bot status: " + statusStr;

        Platform.runLater(() -> statusLabel.setText(statusMessage));
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    private void loadTable() {
        commandTable.setItems(FXCollections.observableArrayList(cfgManager.getCFGData()));

        commandColumn.setCellValueFactory(
                new PropertyValueFactory<>("cmd")
        );
        messageColumn.setCellValueFactory(
                new PropertyValueFactory<>("msg")
        );
    }
}
