package org.prog3.email.client.model.tasks;

import org.prog3.email.model.Request;
import org.prog3.email.model.RequestType;
import org.prog3.email.model.Email;

import java.io.IOException;

public class SendMessage extends ClientTask {
    Email email;

    /*
     * Task to send Email
     */
    public SendMessage (String account, Email email) {
        SendMessage.account = account;
        this.email = email;
    }

    @Override
    public void run() {
        if (!checkConnection()) {
            return;
        }

        Request request = new Request(RequestType.PushMessage, email);

        synchronized (lock) {
            try {
                out.writeObject(request);
                out.flush();
                System.out.println("Sent PushMessage Request");
                Object response = in.readObject(); // wait response
                System.out.println("Server: " + response);
                if (response instanceof String r) { // sent correctly
                    if (r.equals("OK")) {
                        controller.notify("eMail Sent");
                    } else if (r.equals("ERROR: Bad Request")) {
                        controller.notify("Error, retrying");
                        out.writeObject(request);
                        out.flush();
                    } else {
                        controller.notify(r);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

