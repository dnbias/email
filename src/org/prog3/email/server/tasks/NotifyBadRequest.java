package org.prog3.email.server.tasks;

import org.prog3.email.AppendingObjectOutputStream;
import org.prog3.email.server.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NotifyBadRequest extends ServerTask {
    public NotifyBadRequest(ObjectOutputStream out, ObjectInputStream in) {
        super(out, in);
    }

    @Override
    public void run() {
        try {
            out.writeObject("Error: Bad Request");
            out.flush();

            Logger.log(socket + " - Bad Request notified" + socket);
            closeStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
