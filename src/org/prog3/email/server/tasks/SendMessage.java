package org.prog3.email.server.tasks;

import org.prog3.email.model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

public class SendMessage extends ServerTask {
    Email email;
    public SendMessage (String account, Email email, ObjectOutputStream out, ObjectInputStream in) {
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

            LinkedList<ObjectOutputStream> connectedReceivers = model.getConnectedOutputStreams(email.getReceivers());
            if (connectedReceivers.size() > 0) {
                for (int i = 0; i < connectedReceivers.size(); i++) {
                    synchronized (connectedReceivers.get(i)) {
                        connectedReceivers.get(i).writeObject("New eMail Received");
                        connectedReceivers.get(i).flush();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

