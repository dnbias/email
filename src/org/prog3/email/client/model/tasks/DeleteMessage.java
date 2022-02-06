package org.prog3.email.client.model.tasks;

import javafx.collections.ObservableList;
import org.prog3.email.Request;
import org.prog3.email.RequestType;
import org.prog3.email.model.Email;

import java.io.IOException;

public class DeleteMessage extends ClientTask {
    Email email;

    public DeleteMessage(String host, int port, String account, Email email, ObservableList<Email> inbox) {
        super(host, port, account);
        this.email = email;
        this.inbox = inbox;
    }

    @Override
    public void run() {
        try {
            connectToServer(host, port);
            Request request = new Request(RequestType.DeleteMessage, email);
            out.writeObject(request);
            out.flush();

            inbox.remove(email);

            closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}