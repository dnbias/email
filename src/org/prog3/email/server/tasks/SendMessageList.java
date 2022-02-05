package org.prog3.email.server.tasks;

import org.prog3.email.model.*;
import org.prog3.email.server.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class SendMessageList extends ServerTask {

    public SendMessageList(String account, Model model, Socket socket) {
        super(account, model, socket);
    }

    @Override
    public Void call() {
        try {
            ArrayList<Email> emails = model.getEmails(account);
            out.writeObject(emails);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.log("Sent emails list: " + account);
        closeStreams();
        return null;
    }
}
