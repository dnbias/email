package org.prog3.email.client.model.tasks;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import org.prog3.email.client.model.Client;
import org.prog3.email.client.ui.ClientController;
import org.prog3.email.model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;

public abstract class ClientTask implements Runnable {
    static Client client;
    static ClientController controller;
    final static Object lock = new Object();
    static String account, host;
    static int port;
    static Socket socket;
    static ObjectInputStream in;
    static ObjectOutputStream out;
    ObservableList<Email> inbox;
    static BooleanProperty ongoingConnection;

    public ClientTask() { }

    public static void initialize(Client clientRef, ClientController clientController, BooleanProperty connected) {
        client = clientRef;
        controller = clientController;
        ongoingConnection = connected;
    }

    public static void initialize(ClientController clientController, BooleanProperty connected, String currentAccount) {
        controller = clientController;
        ongoingConnection = connected;
        account = currentAccount;
    }

    public static void setAccount(String currentAccount) {
        account = currentAccount;
    }

    public ClientTask(String host, int port) {
        ClientTask.host = host;
        ClientTask.port = port;
    }

    protected synchronized boolean checkConnection() {
        if (!ongoingConnection.getValue()) {
            System.out.println("No connection");
            controller.notify("No Connection");
        }

        // check if there pending notifications
        try {
            Request request = new Request(RequestType.CheckNotifications, account);
            out.writeObject(request);
            out.flush();
            Object response = in.readObject();
            if (response instanceof  String r) {
                if (r.startsWith("Notifications:")) {
                    response = in.readObject();
                    if (response instanceof LinkedList<?> notifications) {
                        String msg = "New Notifications:\n";
                        for (int i = 0; i < notifications.size(); i++) {
                            Object n = notifications.get(i);
                            if (n instanceof String ns) {
                                msg += ns + "\n";
                            }
                        }
                        controller.notify(msg);
                        client.pullEmails();
                    }
                }
            }
        } catch (SocketException s) {
            controller.notify("No Connection");
            Platform.runLater(() -> ongoingConnection.set(false));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return ongoingConnection.getValue();
    }
}
