package org.prog3.email.server.tasks;

import org.prog3.email.model.Email;
import org.prog3.email.model.Model;
import org.prog3.email.server.Logger;

import java.io.IOException;
import java.net.Socket;

public class SendMessage extends ServerTask {
    Email email;
    public SendMessage (String account, Email email, Model model, Socket socket) {
        super(account, model, socket);
        this.email = email;
    }

    @Override
    public Void call() throws Exception {
        try {
            model.addEmail(email);
            out.writeObject("OK");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.log("Sent email - " + "from:"+ account + " to:" + email.getReceivers());
        closeStreams();
        return null;
    }
}

