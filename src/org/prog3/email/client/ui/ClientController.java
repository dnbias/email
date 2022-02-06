package org.prog3.email.client.ui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.prog3.email.model.Email;

import java.io.PrintStream;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.prog3.email.client.model.*;

public class ClientController {
    @FXML
    TextField lblFrom, lblTo, lblSubject, lblDate;

    @FXML
    ChoiceBox<String> boxAccount;

    @FXML
    TextArea txtBody;

    @FXML
    TableView<Email> tableEmails;

    @FXML
    TableColumn<Email, List<String>> tableColTo;

    @FXML
    TableColumn<Email, String> tableColFrom, tableColSubject, tableColDate;

    @FXML
    Button btnCompose, btnReply, btnDelete, btnForward;

    private Client client;
    private Email selectedEmail;
    private Email emptyEmail;

    private boolean composingContent = false;
    private SimpleBooleanProperty composing; // TODO

    public ClientController(PrintStream out) {
        System.setOut(out);
    }

    @FXML
    public void initialize(Client model){
        if (client != null)
            throw new IllegalStateException("Model can only be initialized once");

        client = model;
        selectedEmail = null;

        ObservableList<Email> data = FXCollections.observableArrayList();
        tableEmails.setItems(model.inboxProperty());
        tableEmails.setOnMouseClicked(this::showSelectedEmail);
        setTableCellFactories();
        boxAccount.itemsProperty().bind(model.accountProperty());
        emptyEmail = new Email("", "", "", "", Calendar.getInstance().getTime());

        updateDetailView(emptyEmail);

        System.out.println("Try and pull emails from server");
        client.pullEmails();

        Email test = new Email("pippo@digi.com", "account@unito.it",
                "Test","TestTest",Calendar.getInstance().getTime());
        client.sendEmail(test);
    }


    void setTableCellFactories() {
        tableColSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        tableColFrom.setCellValueFactory(new PropertyValueFactory<>("sender"));
        tableColTo.setCellValueFactory(new PropertyValueFactory<>("receivers"));
        tableColDate.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().getDate().toString())));
    }


    @FXML
    void onDeleteButtonClick() {
        if (selectedEmail != null) {
            client.deleteEmail(selectedEmail);
            updateDetailView(emptyEmail);
        }
    }

    @FXML
    void onReplyButtonClick() {
        Email selected = tableEmails.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Email newEmail =
                    new Email(boxAccount.getValue(), selected.getReceivers(),
                            selected.getSubject(), "", Calendar.getInstance().getTime());
            updateDetailView(newEmail);
        }
    }

    @FXML
    void onForwardButtonClick() {
        Email selected = tableEmails.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Email newEmail =
                    new Email(boxAccount.getValue(), selected.getReceivers(),
                            "fwd: " + selected.getSubject(),
                            selected.getBody(), Calendar.getInstance().getTime());
            updateDetailView(newEmail);
        }
    }

    @FXML
    void onComposeButtonClick() {
        updateDetailView(emptyEmail);
        composingContent = true;
    }

    protected void showSelectedEmail(MouseEvent mouseEvent) {
        Email email = tableEmails.getSelectionModel().getSelectedItem();

        selectedEmail = email;
        updateDetailView(email);
    }

    protected void updateDetailView(Email email) {
        if(email != null) {
            System.out.println("Updating Detail View");
            lblFrom.setText(email.getSender());
            lblTo.setText(String.join(", ", email.getReceivers()));
            lblDate.setText(email.getDate().toString());
            lblSubject.setText(email.getSubject());
            txtBody.setText(email.getBody());
        }
    }

}

