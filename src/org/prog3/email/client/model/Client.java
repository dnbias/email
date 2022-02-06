package org.prog3.email.client.model;

import javafx.beans.property.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;


import org.prog3.email.model.Email;
import org.prog3.email.client.ui.*;
import org.prog3.email.client.model.tasks.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

public class Client extends Application {

    ClientController controller;
    ObjectOutputStream outputStream = null;
    ObjectInputStream inputStream = null;
    String host, id = UUID.randomUUID().toString();
    int port, currentAccount = 0;

    private static final int NUM_THREADS = 5;
    private static ThreadPoolExecutor executor = null;
    private static Vector<ClientTask> tasks = null;

    SimpleListProperty<Email> inbox;
    ObservableList<Email> inboxContent;
    SimpleListProperty<String> accounts;
    ObservableList<String> accountsContent;

    public Client() {
        accountsContent = FXCollections.observableList(new LinkedList<>());
        accountsContent.add("account@unito.it");
        accounts = new SimpleListProperty<>();
        accounts.set(accountsContent);
        host = "localhost";
        port = 8888;
        inboxContent = FXCollections.observableList(new LinkedList<>());
        inbox = new SimpleListProperty<>();
        inbox.set(inboxContent);
        controller = new ClientController(System.out);
    }

    public Client(String account, String host, int port) {
        super();
        this.host = host;
        this.port = port;
        accountsContent.add(account);
    }

    @Override
    public void start(Stage stage) throws Exception{
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUM_THREADS);
        tasks = new Vector<ClientTask>();

        URL url = getClass().getResource("/client.fxml");

        assert url != null;

        FXMLLoader loader = new FXMLLoader(url);
        loader.setController(controller);
        stage.setTitle("Email Client");
        stage = loader.load();
        stage.setHeight(800);
        stage.setWidth(1000);
        stage.show();
        System.out.println("Executed...");
        controller.initialize(this);
    }

    public ListProperty<Email> inboxProperty() {
        return inbox;
    }

    public ListProperty<String> accountProperty() {
        return accounts;
    }

    public void pullEmails() {
        System.out.println("Pulling emails");
        ClientTask task = new PullMessageList(host, port, accountsContent.get(currentAccount), inboxContent);
        tasks.add(task);
        executor.execute(task);
    }

    public void sendEmail(Email email) {
        System.out.println("Sending email");
        ClientTask task = new SendMessage(host, port, accountsContent.get(currentAccount), email);
        tasks.add(task);
        executor.execute(task);
    }

    public void deleteEmail(Email email) {
        System.out.println("Deleting email");
        ClientTask task = new DeleteMessage(host, port, accountsContent.get(currentAccount), email, inboxContent);
        tasks.add(task);
        executor.execute(task);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
