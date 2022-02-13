package org.prog3.email.client.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class NotificationController {
    @FXML
    Button bntOk;

    @FXML
    Stage stgNotification;

    @FXML
    Label lblMessage;

    /*
     * Sets the text on the notification message
     */
    public void setNotification(String string) {
        lblMessage.setText(string);
    }

    @FXML
    protected void onOkButtonClick() {
        stgNotification.close();
    }
}
