package org.prog3.email.client.ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.prog3.email.model.Email;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.*;

import org.prog3.email.client.model.*;

public class ClientController {
    @FXML
    TextField lblFrom, lblTo, lblSubject, lblDate, fieldAccount;

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
    Button btnCompose, btnReply, btnDelete, btnForward, btnSend, btnConfirm, btnConnection;

    SimpleBooleanProperty connected, composing;

    private Client client;
    private Email selectedEmail;
    private Email emptyEmail;

    private Stage notification, promptForAccount;
    NotificationController notificationController;


    public ClientController(PrintStream out) {
        System.setOut(out);
    }


    /*
     * Initialize the main controller and the notification controller
     */
    @FXML
    public void initialize(Client model){
        if (client != null)
            throw new IllegalStateException("Model can only be initialized once");

        client = model;

        emptyEmail = new Email(
                boxAccount.getValue(), "", "", "", Calendar.getInstance().getTime());
        notificationController = new NotificationController();
        setUpNotification(); // notification controller

        setUpPrompt();

        selectedEmail = null; // empty email editor

        // setup properties and listeners
        tableEmails.setItems(model.inboxProperty());
        tableEmails.setOnMouseClicked(this::showSelectedEmail);
        tableEmails.getSortOrder().add(tableColDate);
        tableEmails.sort();
        setTableCellFactories();

        // show the emptyEmail
        updateDetailView(emptyEmail, false);

        client.checkConfiguration();
    }

    /*
     * Open a notification window showing @param message
     */
    public void notify(String message) {
        notificationController.setNotification(message);
        Platform.runLater( () -> notification.show() );
    }

    public void promptForAccount() {
        Platform.runLater( () -> promptForAccount.show() );
    }

    public void refreshInterface() {
        boxAccount.itemsProperty().bind(client.accountProperty());
        boxAccount.setValue(client.accountProperty().get(
                Configuration.instance.getCurrentAccount()));


        connected = new SimpleBooleanProperty();
        connected.bind(client.connectedProperty());
        ChangeListener<Boolean> connectionListener =
                (observableValue, booleanProperty, t1) -> changeStatus(observableValue.getValue());
        connected.addListener(connectionListener);

        composing = new SimpleBooleanProperty();
        composing.set(false);
        composing.bind(btnSend.disableProperty());
    }

    public void reorderTable() {
        tableEmails.sort();
    }

    // set up the notification controller
    private void setUpNotification() {
        URL url = getClass().getResource("/notification.fxml");

        assert url != null;

        FXMLLoader loader = new FXMLLoader(url);

        loader.setController(notificationController);

        notification = new Stage();
        notification.setTitle(boxAccount.getValue());
        try {
            notification = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        notification.setHeight(200);
        notification.setWidth(300);
        notification.getIcons().add(
                new Image(Client.class.getResourceAsStream("/email.png")));
        notification.setAlwaysOnTop(true);
        notification.initStyle(StageStyle.UNDECORATED);

        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(notification.getScene());
    }

    // Set up the account prompt (at startup with no configuration)
    private void setUpPrompt() {
        URL url = getClass().getResource("/prompt.fxml");

        assert url != null;

        FXMLLoader loader = new FXMLLoader(url);

        loader.setController(new PromptController());

        promptForAccount = new Stage();
        promptForAccount.setTitle("Prompt");
        try {
            promptForAccount = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        promptForAccount.setHeight(200);
        promptForAccount.setWidth(300);
        promptForAccount.getIcons().add(
                new Image(Client.class.getResourceAsStream("/email.png")));
        promptForAccount.setAlwaysOnTop(true);
        promptForAccount.initStyle(StageStyle.UNDECORATED);

        JMetro jMetro = new JMetro(Style.LIGHT);
        jMetro.setScene(promptForAccount.getScene());
    }

    // Set up the values for the Inbox
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
            updateDetailView(emptyEmail, false);
        }
    }

    @FXML
    void onReplyButtonClick() {
        Email selected = tableEmails.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Email newEmail =
                    new Email(boxAccount.getValue(), selected.getSender(),
                            "Reply: "+selected.getSubject(), "", Calendar.getInstance().getTime());
            updateDetailView(newEmail, true);
        }
    }

    @FXML
    void onForwardButtonClick() {
        Email selected = tableEmails.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Email newEmail =
                    new Email(boxAccount.getValue(), "",
                            "fwd: " + selected.getSubject(),
                            selected.getBody(), Calendar.getInstance().getTime());
            updateDetailView(newEmail, true);
        }
    }

    @FXML
    void onComposeButtonClick() {
        emptyEmail = new Email(
                boxAccount.getValue(), "", "", "", Calendar.getInstance().getTime());
        selectedEmail = emptyEmail;
        updateDetailView(selectedEmail, true);
    }

    @FXML
    void onSendButtonClick() {
        client.sendEmail(getDetailViewEmail());
        selectedEmail = emptyEmail;
        updateDetailView(selectedEmail, false);
    }

    @FXML
    void onConnectionClick() {
        client.establishConnection();
        if (connected.getValue()) {
            notify("Connected");
        } else {
            notify("Disconnected");
        }
    }

    protected void showSelectedEmail(MouseEvent mouseEvent) {
        Email email = tableEmails.getSelectionModel().getSelectedItem();

        selectedEmail = email;
        updateDetailView(email, false);
    }


    // Shows the email in the editor and make it editable or not
    protected void updateDetailView(Email email, boolean editable) {
        if(email != null) {
            System.out.println("Updating Detail View");
            lblFrom.setText(email.getSender());
            lblTo.setText(String.join(", ", email.getReceivers()));
            lblDate.setText(email.getDate().toString());
            lblSubject.setText(email.getSubject());
            txtBody.setText(email.getBody());

            lblFrom.setEditable(false); // always the current account
            lblDate.setEditable(false); // and the correct date for the context

            lblTo.setEditable(editable);
            lblSubject.setEditable(editable);
            txtBody.setEditable(editable);

            selectedEmail = email;
        }
    }

    // Return the email fetching the text in the editor
    protected  Email getDetailViewEmail() {
        Email e = null;
        ArrayList<String> receivers = extract(lblTo.getText());
        if (receivers.size() == 0) {
            notify("Error: no receivers");
        } else if (receivers.size() == 1) {
            e = new Email(
                   selectedEmail.getSender(),
                   receivers.get(0),
                   lblSubject.getText(),
                   txtBody.getText(),
                   selectedEmail.getDate()
            );
        } else {
            e = new Email(
                    selectedEmail.getSender(),
                    receivers,
                    lblSubject.getText(),
                    txtBody.getText(),
                    selectedEmail.getDate()
            );
        }

        return e;
    }

    // extracts the correctly formatted accounts from @param s
    // removing the left and right brackets and spaces
    private ArrayList<String> extract(String listOfAccounts) {
        ArrayList<String> r = new ArrayList<>();
        if (listOfAccounts.toCharArray()[0] == '[') {
            char[] chars = new char[listOfAccounts.length()-2];
            listOfAccounts.getChars(1,listOfAccounts.length(),chars,0);
            listOfAccounts = new String(chars);
        }
        StringTokenizer st = new StringTokenizer(listOfAccounts, ", ");

        while (st.hasMoreTokens()) {
            r.add(st.nextToken());
        }

        return  r;
    }

    // listener for the connection state
    protected void changeStatus(Boolean connected) {
        if (connected) {
            btnConnection.setStyle("-fx-background-image: url('connected.png')");
            client.pullEmails();
        } else {
            btnConnection.setStyle("-fx-background-image: url('disconnected.png')");
        }
    }

    // listener for the composing state
    protected void changeComposing(Boolean composing) {
        if (composing) {
            btnSend.setDisable(false);
        }
    }

}

