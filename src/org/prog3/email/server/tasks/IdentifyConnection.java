package org.prog3.email.server.tasks;

import javafx.application.Platform;
import org.util.logger.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IdentifyConnection extends ServerTask {
    public IdentifyConnection(ObjectOutputStream out, ObjectInputStream in, String account) {
        super(out,in);
        this.account = account;
    }

    @Override
    public void run() {
        try {
            synchronized (out) {
                out.writeObject("OK, ID: " + account);
                out.flush();
            }
            model.addClient(account, out);
        } catch (IOException e) {
            Logger.log(e.getMessage());
            e.printStackTrace();
        }
    }
}
