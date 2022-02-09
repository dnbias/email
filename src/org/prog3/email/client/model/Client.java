package org.prog3.email.client.model;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.prog3.email.model.Email;
import org.prog3.email.client.ui.*;
import org.prog3.email.client.model.tasks.*;

import java.net.URL;
import java.util.LinkedList;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Client extends Application {

    ClientController controller;
    String host, id = UUID.randomUUID().toString();
    int port, currentAccount = 0;

    private static final int NUM_THREADS = 5;
    private static ThreadPoolExecutor executor = null;
    private static Vector<ClientTask> tasks = null;

    SimpleListProperty<Email> inbox;
    ObservableList<Email> inboxContent;
    SimpleListProperty<String> accounts;
    ObservableList<String> accountsContent; // a client can have multiple accounts
    SimpleBooleanProperty connected;
    boolean connectedValue = false;

    public Client() {
        accountsContent = FXCollections.observableList(new LinkedList<>());
        accountsContent.add("account@unito.it"); // default DEBUG account
        accounts = new SimpleListProperty<>();
        accounts.set(accountsContent);
        host = "localhost";
        port = 8888;
        inboxContent = FXCollections.observableList(new LinkedList<>());
        inbox = new SimpleListProperty<>();
        inbox.set(inboxContent);
        connected = new SimpleBooleanProperty();
        connected.set(connectedValue);
        controller = new ClientController(System.out);
        ClientTask.initialize(controller, connected, accountsContent.get(currentAccount));
    }

    /*
     * Constructor initializing the client
     * @param account of the email connection
     * @param host of the server socket
     * @param port of the server socket
     */
    public Client(String account, String host, int port) {
        super();
        this.host = host;
        this.port = port;
        accountsContent.add(account);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(true);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUM_THREADS);
        tasks = new Vector<>();
        SetupShutdown();

        URL url = getClass().getResource("/client.fxml");

        assert url != null;

        FXMLLoader loader = new FXMLLoader(url);
        loader.setController(controller);
        stage.setTitle("Email Client");
        stage = loader.load();
        stage.setHeight(800);
        stage.setWidth(1000);
        stage.getIcons().add(new Image(Client.class.getResourceAsStream("/email.png")));

        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(stage.getScene());

        stage.show();
        stage.setOnCloseRequest(e -> System.exit(0));

        System.out.println("Executed...");
        controller.initialize(this);
    }

    public ListProperty<Email> inboxProperty() {
        return inbox;
    }

    public ListProperty<String> accountProperty() {
        return accounts;
    }

    public BooleanProperty connectedProperty() { return connected; }

    /*
     * Connect to the server, identify and pull emails if successful
     */
    public void establishConnection() {
        System.out.println("Connecting to server");
        ClientTask task = new ConnectToServer(host, port, accountsContent.get(currentAccount));
        tasks.add(task);
        executor.execute(task);
    }

    /*
     * Pull email in the inbox
     */
    public void pullEmails() {
        System.out.println("Pulling emails");
        ClientTask task = new PullMessageList(accountsContent.get(currentAccount), inboxContent);
        tasks.add(task);
        executor.execute(task);
    }

    /*
     * Send email to the server
     */
    public void sendEmail(Email email) {
        System.out.println("Sending email");
        ClientTask task = new SendMessage(accountsContent.get(currentAccount), email);
        tasks.add(task);
        executor.execute(task);
    }

    /*
     * Delete email to the server
     */
    public void deleteEmail(Email email) {
        System.out.println("Deleting email");
        ClientTask task = new DeleteMessage(accountsContent.get(currentAccount), email, inboxContent);
        tasks.add(task);
        executor.execute(task);
    }

    /*
     * Close connection to the server
     */
    public void closeConnection() {
        System.out.println("Closing Connection");
        ClientTask task = new CloseConnection();
        tasks.add(task);
        executor.execute(task);
    }

    public static void main(String[] args) {
        launch(args);
    }

    // setup shutdown hook, close connection if present
    static void SetupShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            CloseConnection t = new CloseConnection();
            t.run();
        }));
    }
}
