package org.prog3.email.client.model.tasks;

import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import org.prog3.email.client.ui.ClientController;
import org.prog3.email.model.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class ClientTask implements Runnable {
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

    public static void initialize(ClientController clientController, BooleanProperty connected, String currentAccount) {
        controller = clientController;
        ongoingConnection = connected;
        account = currentAccount;
    }

    public ClientTask(String host, int port) {
        ClientTask.host = host;
        ClientTask.port = port;
    }

    protected boolean checkConnection() {
        if (!ongoingConnection.getValue()) {
            System.out.println("No connection");
            controller.notify("No Connection");
        }
        return ongoingConnection.getValue();
    }
}
