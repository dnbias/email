package org.prog3.email.server.tasks;

import org.prog3.email.model.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class ServerTask implements Runnable {
    String account;
    static Model model;
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;

    public ServerTask() {}

    public ServerTask(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }
    public ServerTask(String account, ObjectOutputStream out, ObjectInputStream in) {
        this.account = account;
        this.in = in;
        this.out = out;
    }

    public static void initialize(Model serverModel) {
        model = serverModel;
    }

}

