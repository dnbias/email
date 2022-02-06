package org.prog3.email.server.tasks;

import org.prog3.email.model.*;
import org.util.logger.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DeleteMessage extends ServerTask {
    Email email;

    public DeleteMessage(String account, Email email, Model model, ObjectOutputStream out, ObjectInputStream in) {
        super(account, model, out, in);
        this.email = email;
    }

    @Override
    public void run() {
        try {
            String message = model.deleteEmail(email) ? "OK" : "ERROR";
            out.writeObject(message);
            out.flush();
            Logger.log("Deleted email: " +
                    account + "/" + email.getSender() + "/" + email.getDate().getTime() + ".json");
            closeStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }

   }
}