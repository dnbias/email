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
    TextField lblFrom;

    @FXML
    TextField lblTo;

    @FXML
    TextField lblSubject;

    @FXML
    public TextField lblDate;

    @FXML
    public ChoiceBox<String> boxAccount;

    @FXML
    public TextArea txtBody;

    @FXML
    public TableView<Email> tableEmails;

    @FXML
    public TableColumn<Email, String> tableColFrom;

    @FXML
    public TableColumn<Email, List<String>> tableColTo;

    @FXML
    public TableColumn<Email, String> tableColSubject;

    @FXML
    public TableColumn<Email, String> tableColDate;

    @FXML
    public Button btnCompose, btnReply, btnDelete, btnForward;

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


    public void setTableCellFactories() {
        tableColSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        tableColFrom.setCellValueFactory(new PropertyValueFactory<>("sender"));
        tableColTo.setCellValueFactory(new PropertyValueFactory<>("receivers"));
        tableColDate.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().toString())));
    }


    @FXML
    public void onDeleteButtonClick() {
        if (selectedEmail != null) {
            client.deleteEmail(selectedEmail);
            updateDetailView(emptyEmail);
        }
    }

    @FXML
    public void onReplyButtonClick() {
        Email selected = tableEmails.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Email newEmail =
                    new Email(boxAccount.getValue(), selected.getReceivers(),
                            selected.getSubject(), "", Calendar.getInstance().getTime());
            updateDetailView(newEmail);
        }
    }

    @FXML
    public void onForwardButtonClick() {
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
    public void onComposeButtonClick() {
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

