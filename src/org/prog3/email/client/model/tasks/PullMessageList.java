package org.prog3.email.client.model.tasks;

import javafx.collections.ObservableList;
import org.prog3.email.Request;
import org.prog3.email.RequestType;
import org.prog3.email.model.Email;

import java.io.EOFException;
import java.io.IOException;

public class PullMessageList extends ClientTask {

    public PullMessageList(String host, int port, String account, ObservableList<Email> inbox) {
        super(host, port, account);
        this.inbox = inbox;
    }

    @Override
    public void run() {
        try {
            connectToServer(host, port);

            Request request = new Request(RequestType.PullMessages, account);
            out.writeObject(request);
            out.flush();

            System.out.println("Sent Pull Request");

            Object input = in.readObject(); // wait for OK to connection message
            if (input instanceof String s && s.equals("OK")) {
                System.out.println("Received OK");
            } else {
                System.out.println("Error during MailInbox stream");
                closeConnection();
                return;
            }

            inbox.removeAll();
            System.out.println("Read input");
            input = in.readObject();


            while (input instanceof Email e) {
                inbox.add(e);
                input = in.readObject();
            }


            if (input instanceof String m) {
                if (m.equals("End of stream")) {
                    System.out.println("MailInbox stream finished: " + m);
                } else {
                    System.out.println("Error during MailInbox stream");
                }
            }
        } catch (EOFException eof) {
            System.out.println("MailInbox stream finished");
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
}
