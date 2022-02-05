package org.prog3.email.client;

import javafx.application.Application;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.prog3.email.Request;
import org.prog3.email.RequestType;
import org.prog3.email.model.Email;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

public class Client extends Application {

    ClientController controller;
    Socket socket = null;
    ObjectOutputStream outputStream = null;
    ObjectInputStream inputStream = null;
    String host;
    int id, port;
    final int MAX_ATTEMPTS = 5;

    ListProperty<Email> inbox;
    ObservableList<Email> inboxContent;
    StringProperty account;


    public Client(int id, String account, String host, int port) {
        this.host = host;
        this.port = port;

        this.id = id;
        this.inboxContent = FXCollections.observableList(new LinkedList<>());
        this.inbox = new SimpleListProperty<>();
        this.inbox.set(inboxContent);
        this.account = new SimpleStringProperty(account);

        controller = new ClientController();
    }

    @Override
    public void start(Stage stage) throws Exception{
        URL clientUrl = Client.class.getResource("client.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(clientUrl);
        stage.setTitle("Email Client");
        stage.setScene(new Scene(fxmlLoader.load(), 900, 800));
        stage.show();
    }

    public ListProperty<Email> inboxProperty() {
        return inbox;
    }

    public StringProperty accountProperty() {
        return account;
    }

    public void communicate(String host, int port){
        int attempts = 0;

        boolean success = false;
        while(attempts < MAX_ATTEMPTS && !success) {
            attempts += 1;
            System.out.println("[Client "+ this.id +"] Tentativo nr. " + attempts);

            success = tryCommunication(host, port);

            if(success) {
                continue;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean tryCommunication(String host, int port) {
        try {
            connectToServer(host, port);

            return true;
        } catch (ConnectException ce) {
            // nothing to be done
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnections();
        }
    }

    private void closeConnections() {
        if (socket != null) {
            try {
                inputStream.close();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void pullEmails() throws  IOException, ClassNotFoundException {
        Request request = new Request(RequestType.PullMessages, account.getValue());
        outputStream.writeObject(request);
        outputStream.flush();

        Object input = inputStream.readObject();
        if (input instanceof String s) {
            if (!s.equals("OK")) {
                System.out.println("Error during MailInbox stream");
                return;
            }
        }

        inboxContent.removeAll();
        input = inputStream.readObject();
        while (input instanceof Email e) {
            inboxContent.add(e);
            input = inputStream.readObject();
        }

        if (input instanceof  String s) {
            if (s.equals("End of stream")) {
                System.out.println("MailInbox stream finished: " + s);
            } else {
                System.out.println("Error during MailInbox stream");
            }
        }
    }

    public void sendEmail(Email email) throws IOException {
        Request request = new Request(RequestType.PushMessage, email);
        outputStream.writeObject(request);
        outputStream.flush();
    }

    public void deleteEmail(Email email) throws IOException {
        Request request = new Request(RequestType.DeleteMessage, email);
        outputStream.writeObject(request);
        outputStream.flush();

        inboxContent.remove(email);
    }


    private void connectToServer(String host, int port) throws IOException {
        socket = new Socket(host, port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());

        // callers may wish to flush the stream immediately to ensure that constructors for receiving
        // ObjectInputStreams will not block when reading the header.
        outputStream.flush();

        inputStream = new ObjectInputStream(socket.getInputStream());

        System.out.println("[Client "+ this.id + "] Connected");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
