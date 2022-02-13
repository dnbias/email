package org.prog3.email.server.tasks;

import org.prog3.email.model.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DeleteEmail extends ServerTask {
    Email email;

    /*
     * Task to delete Email from client's account
     */
    public DeleteEmail(String account, Email email, ObjectOutputStream out, ObjectInputStream in) {
        super(account, out, in);
        this.email = email;
    }

    @Override
    public void run() {
        try {
            String message = model.deleteEmail(account, email) ? "OK" : "ERROR";
            synchronized (out) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

   }
}