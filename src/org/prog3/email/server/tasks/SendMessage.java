package org.prog3.email.server.tasks;

import org.prog3.email.model.*;
import org.util.logger.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

