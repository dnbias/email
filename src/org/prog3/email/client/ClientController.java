package org.prog3.email.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import org.prog3.email.model.Email;

import java.io.IOException;
import java.util.Calendar;

public class ClientController {
    @FXML
    private Label lblFrom;

    @FXML
    private Label lblTo;

    @FXML
    private Label lblSubject;

    @FXML
    private Label lblDate;

    @FXML
    private Label boxAccount;

    @FXML
    private TextArea txtBody;

    @FXML
    private TableView<Email> tableEmails;

    private Client client;
    private Email selectedEmail;
    private Email emptyEmail;

    private boolean composing = false;

    @FXML
    public void initialize(Client model){
        if (client != null)
            throw new IllegalStateException("Model can only be initialized once");

        client = model;

        selectedEmail = null;

        ObservableList<Email> data = FXCollections.observableArrayList();

        tableEmails.itemsProperty().bind(model.inboxProperty());
        tableEmails.setOnMouseClicked(this::showSelectedEmail);
        boxAccount.textProperty().bind(model.accountProperty());

        emptyEmail = new Email("", "", "", "", Calendar.getInstance());

        updateDetailView(emptyEmail);

        try {
            client.pullEmails();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onDeleteButtonClick() {
        try {
            client.deleteEmail(selectedEmail);
            updateDetailView(emptyEmail);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onComposeButtonClick() {
        updateDetailView(emptyEmail);
        composing = true;
    }

    protected void showSelectedEmail(MouseEvent mouseEvent) {
        Email email = tableEmails.getSelectionModel().getSelectedItem();

        selectedEmail = email;
        updateDetailView(email);
    }

    protected void updateDetailView(Email email) {
        if(email != null) {
            lblFrom.setText(email.getSender());
            lblTo.setText(String.join(", ", email.getReceivers()));
            lblDate.setText(email.getDate().toString());
            lblSubject.setText(email.getSubject());
            txtBody.setText(email.getBody());
        }
    }

}

