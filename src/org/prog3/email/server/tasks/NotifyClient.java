package org.prog3.email.server.tasks;

import org.util.logger.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class NotifyClient extends ServerTask {
    String message = null;

    public NotifyClient(ObjectOutputStream out, ObjectInputStream in) {
        super(out, in);
    }

    public NotifyClient(ObjectOutputStream out, ObjectInputStream in, String message) {
        super(out, in);
        this.message = message;
    }

    @Override
    public void run() {
        try {
            synchronized (out) {
                if (message != null) {
                    out.writeObject(message);
                } else {
                    out.writeObject("Error: Bad Request");
                }
                out.flush();
            }

            Logger.log(socket, "Bad Request notified");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
