package org.prog3.email.server.tasks;

import org.prog3.email.model.*;
import org.util.logger.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

public class SendEmail extends ServerTask {
    Email email;
    /*
     * Task to send Email to Server
     */
    public SendEmail(String account, Email email, ObjectOutputStream out, ObjectInputStream in) {
        super(account, out, in);
        this.email = email;
    }

    @Override
    public void run() {
        try {
            model.addEmail(email);
            synchronized (out) {
                out.writeObject("OK");
                out.flush();
            }

            LinkedList<String> connectedReceivers = model.getConnected(email.getReceivers());
            if (connectedReceivers.size() > 0) {
                for (int i = 0; i < connectedReceivers.size(); i++) {
                    Logger.log("Notifying receiver " + connectedReceivers.get(i));
                    String msg = "New eMail Received from " + email.getSender();
                    model.addPendingNotification(connectedReceivers.get(i), msg);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

