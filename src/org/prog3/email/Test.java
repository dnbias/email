package org.prog3.email;

import javafx.application.Application;
import javafx.stage.Stage;
import org.prog3.email.client.model.Client;
import org.prog3.email.server.Server;


import java.util.ArrayList;

public class Test {
    static int nClients = 4;

    public static void main(String[] args) {
        try {
            Application.launch(Server.class);
            for (int i = 0; i < nClients; i++ ) {
                Application.launch(Client.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
