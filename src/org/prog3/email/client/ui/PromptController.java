package org.prog3.email.client.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.prog3.email.client.model.Configuration;
import org.prog3.email.client.model.tasks.WriteConfiguration;

public class PromptController {

    @FXML
    Stage stagePrompt;

    @FXML
    TextField fieldAccount;

    @FXML
    void confirmPrompt() {
        Configuration.instance.addAccount(fieldAccount.getText());
        Platform.runLater(new WriteConfiguration());
        stagePrompt.close();
    }
}
