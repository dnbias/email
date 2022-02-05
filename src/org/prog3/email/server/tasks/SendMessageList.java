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
            Logger.log(socket + " - Got email list (" + account + ")");
            out.writeObject("OK");
            for (Email email : emails) {
                out.writeObject(email);
                out.flush();
            }
            out.writeObject("End of stream");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.log(socket + " - Sent email list(" + account + ")");
        closeStreams();
        return null;
    }
}
