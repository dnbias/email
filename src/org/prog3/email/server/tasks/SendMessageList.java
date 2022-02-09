package org.prog3.email.server.tasks;

import org.prog3.email.model.*;
import org.util.logger.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SendMessageList extends ServerTask {

    public SendMessageList(String account, ObjectOutputStream out, ObjectInputStream in) {
        super(account, out, in);
    }

    @Override
    public void run() {
        try {
            ArrayList<Email> emails = model.getEmails(account);
            synchronized (out) {
                out.writeObject("OK");
                Logger.log(account + ": " + emails.size() + " emails");
                out.flush();
                for (Email email : emails) {
                    out.writeObject(email);
                    out.flush();
                }
                out.writeObject("End of stream");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
