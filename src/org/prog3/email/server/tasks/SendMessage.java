package org.prog3.email.server.tasks;

import org.prog3.email.AppendingObjectOutputStream;
import org.prog3.email.model.Email;
import org.prog3.email.model.Model;
import org.prog3.email.server.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SendMessage extends ServerTask {
    Email email;
    public SendMessage (String account, Email email, Model model, ObjectOutputStream out, ObjectInputStream in) {
        super(account, model, out, in);
        this.email = email;
    }

    @Override
    public void run() {
        try {
            model.addEmail(email);
            out.writeObject("OK");
            out.flush();
            closeStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

