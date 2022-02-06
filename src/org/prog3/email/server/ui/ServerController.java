package org.prog3.email.server.ui;

import javafx.fxml.FXML;
import org.prog3.email.model.Model;

public class ServerController {

    private Model serverModel = null;
    @FXML
    public void initialize(Model model) {
        if (serverModel != null)
            throw new IllegalStateException("Model can only be initialized once");

        serverModel = model;
    }
}
