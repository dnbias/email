package org.prog3.email.client.model.tasks;

import org.prog3.email.model.Request;
import org.prog3.email.model.RequestType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;

public class ConnectToServer extends ClientTask {

    /*
     * Task to connect to server at host:port and identify as account
     */
    public ConnectToServer(String host, int port, String account) {
        super(host, port);
        ConnectToServer.account = account;
    }

    @Override
    public void run() {
        try {
            boolean identified = false;
            System.out.println("Connecting on " + host + "::" + port);
            try {
                socket = new Socket(host, port);
                System.out.println("OK Socket");
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            } catch (ConnectException e) {
                System.out.println("Connection Refused");
                controller.notify("Connection Refused");
                client.setConnected(false);
                return;
            }
            client.setConnected(true);
            System.out.println("Connected");


            Request request = new Request(RequestType.Identification, account);

            synchronized (lock) {
                while (!identified) {
                    out.writeObject(request);
                    out.flush();
                    System.out.println("Sent ID Request");
                    Object response = in.readObject();
                    if (response instanceof String) {
                        if (response.equals("OK, ID: " + account)) { // all good
                            System.out.println("Identified by server");
                            identified = true;
                        } else {
                            System.out.println(response);
                            controller.notify((String) response);
                        }

                    }
                }
            }
            ongoingConnection.set(true);
            client.checkConnection();

        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
