package org.prog3.email.server.tasks;

import org.util.logger.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

public class PullPendingNotifications extends ServerTask {
    /*
     * Responds to a notification pull by giving a list of notifications if they exist
     */
    public PullPendingNotifications(String account, ObjectOutputStream out, ObjectInputStream in) {
        super(account,out,in);
    }
    @Override
    public void run() {
        LinkedList<String> notifications = model.getPendingNotifications(account);
        String response;
        synchronized (out) {
            try {
                if (notifications.size() > 0) {
                    Logger.log(account + " notifications: " + notifications.size());
                    response = "Notifications: " + notifications.size();
                    out.writeObject(response);
                    out.flush();
                    out.writeObject(notifications);
                    out.flush();
                } else {
                    response = "No Notifications";
                    out.writeObject(response);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
