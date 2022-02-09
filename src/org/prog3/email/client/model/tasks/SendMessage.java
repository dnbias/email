package org.prog3.email.client.model.tasks;

import org.prog3.email.Request;
import org.prog3.email.RequestType;
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
                Object response = in.readObject();
                System.out.println("Server: " + response);
                if (response instanceof String && response.equals("OK")) { // sent correctly
                    controller.notify("eMail Sent");
                } else { // try again
                    out.writeObject(request);
                    out.flush();
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

