package org.prog3.email.server.tasks;

import org.prog3.email.server.Logger;

import java.net.Socket;

public class NotifyBadRequest extends ServerTask {
    public NotifyBadRequest(Socket socket) {
        super(socket);
    }

    @Override
    public Void call() throws Exception {
        out.writeObject("Error: Bad Request");
        out.flush();

        Logger.log(socket + " - Bad Request notified" + socket);
        closeStreams();
        return null;
    }
}
