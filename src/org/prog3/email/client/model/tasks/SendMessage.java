package org.prog3.email.client.model.tasks;

import org.prog3.email.Request;
import org.prog3.email.RequestType;
import org.prog3.email.model.Email;

import java.io.IOException;

public class SendMessage extends ClientTask {
    Email email;
    public SendMessage (String host, int port, String account, Email email) {
        super(host, port, account);
        this.email = email;
    }

    @Override
    public void run() {
        try {
            connectToServer(host, port);
            Request request = new Request(RequestType.PushMessage, email);
            out.writeObject(request);
            out.flush();
            System.out.println("Sent PushMessage Request");
            Object response = in.readObject();
            System.out.println("Server: " + response);
            if (response instanceof String && response.equals("OK")) {
                // sent correctly
            } else { // try again
                out.writeObject(request);
                out.flush();
            }

            closeConnection();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

