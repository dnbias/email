package org.prog3.email.client.model.tasks;

import javafx.collections.ObservableList;
import org.prog3.email.AppendingObjectOutputStream;
import org.prog3.email.model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class ClientTask implements Runnable {
    String account, host;
    int port;
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;
    ObservableList<Email> inbox;

    public ClientTask(String host, int port, String account) {
        this.host = host;
        this.port = port;
        this.account = account;
    }

    protected void connectToServer(String host, int port) throws IOException {
        System.out.println("Connecting on " + host + "::" + port);
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        System.out.println("Connected");
    }

    protected void closeConnection() {
        if (socket != null) {
            try {
                System.out.print("Disconnecting...");
                in.close();
                out.close();
                socket.close();
                System.out.println(" Ok");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
