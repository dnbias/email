package org.prog3.email.client.model.tasks;

import org.prog3.email.model.Request;
import org.prog3.email.model.RequestType;

import java.io.IOException;

public class CloseConnection extends ClientTask {

    public CloseConnection () {
        super();
    }

    @Override
    public void run() {
        if (!ongoingConnection.getValue()) {
            return;
        }

        Request request = new Request(RequestType.CloseConnection, account);

        synchronized (lock) {
            try {
                out.writeObject(request);
                out.flush();

                if (socket != null) {
                    System.out.print("Disconnecting...");
                    in.close();
                    out.close();
                    socket.close();
                    System.out.println(" Ok");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            ongoingConnection.set(false);
        }
    }
}
