package org.prog3.email.client.model.tasks;

import javafx.collections.ObservableList;
import org.prog3.email.model.Request;
import org.prog3.email.model.RequestType;
import org.prog3.email.model.Email;

import java.io.IOException;

public class DeleteMessage extends ClientTask {
    Email email;

    public DeleteMessage(String account, Email email, ObservableList<Email> inbox) {
        DeleteMessage.account = account;
        this.email = email;
        this.inbox = inbox;
    }

    @Override
    public void run() {
        if (!checkConnection()) {
            return;
        }

        Request request = new Request(RequestType.DeleteMessage, account, email);
        synchronized (lock) {
            try {
                out.writeObject(request);
                out.flush();

                inbox.remove(email);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}