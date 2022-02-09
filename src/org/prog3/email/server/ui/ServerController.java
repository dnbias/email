package org.prog3.email.server.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.prog3.email.model.Model;
import org.util.logger.Logger;

public class ServerController {

    private Model serverModel = null;

    @FXML
    ListView<String> listClients, listLog;

    @FXML
    public void initialize(Model model) {
        if (serverModel != null)
            throw new IllegalStateException("Model can only be initialized once");

        serverModel = model;

        listClients.setItems(model.clientsProperty());
        listLog.setItems(model.logProperty());

        Logger.log("Controller initialized");
    }
}
