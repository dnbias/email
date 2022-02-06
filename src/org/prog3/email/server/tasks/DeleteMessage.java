package org.prog3.email.server.tasks;

import org.prog3.email.AppendingObjectOutputStream;
import org.prog3.email.model.*;
import org.prog3.email.server.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
            Logger.log(socket + " - Deleted email: " +
                    account + "/" + email.getSender() + "/" + email.getDate().getTime() + ".json");
            closeStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }

   }
}