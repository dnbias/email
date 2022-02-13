package org.prog3.email.client.model.tasks;

public class CheckConnection extends ClientTask {
    /*
     * Checks the connection asking the server for new notifications
     */
    @Override
    public void run() {
        System.out.print("Connection Check...");
        boolean r = checkConnection();
        String s = r ? "Connected" : "Disconnected";
        System.out.println(s);

        if (!r) {
            client.establishConnection();
        }
    }
}
