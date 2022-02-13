package org.prog3.email.server.ui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.prog3.email.model.Model;
import org.util.logger.Logger;

public class ServerController {

    private Model serverModel = null;

    @FXML
    Label lblNumConnections;

    @FXML
    ListView<String> listClients, listLog;

    @FXML
    public void initialize(Model model) {
        if (serverModel != null)
            throw new IllegalStateException("Model can only be initialized once");

        serverModel = model;

        listClients.setItems(model.clientsProperty());
        listLog.setItems(model.logProperty());
        lblNumConnections.setText("0");

        serverModel.clientsProperty().addListener((obs, oldStatus, newStatus) -> setNumConnections(newStatus));

        Logger.log("Controller initialized");
    }

    public void setNumConnections(ObservableList<String> l) {
        Platform.runLater(() -> lblNumConnections.setText(((Integer) l.size()).toString()));
    }
}
