package org.prog3.email.server.tasks;

import org.util.logger.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CloseConnection extends ServerTask {
    private Socket socket;

    public CloseConnection(Socket socket, String account, ObjectOutputStream out, ObjectInputStream in) {
        super(out, in);
        this.socket = socket;
        this.account = account;
    }

    @Override
    public void run() {
        Logger.log(socket, "Closed Connection Successfully");
        model.removeClient(account);
        try {
            synchronized (out) {
                in.close();
                out.close();
                socket.close();
            }
        } catch (IOException e) {
            Logger.log(e.getMessage());
            e.printStackTrace();
        }
    }
}
