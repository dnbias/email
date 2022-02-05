package org.prog3.email.server.tasks;

import org.prog3.email.model.*;
import org.prog3.email.server.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class DeleteMessage extends ServerTask {
    Email email;

    public DeleteMessage(String account, Email email, Model model, Socket socket) {
        super(account, model, socket);
        this.email = email;
    }

    @Override
    public Void call() {
        try {
            String message = model.deleteEmail(email) ? "OK" : "ERROR";
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.log(socket + " - Deleted email: " + account + "/" + email.getSender() + "/" + email.getDate().getTimeInMillis() + ".json");
        closeStreams();
        return null;
    }
}