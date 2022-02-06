package org.prog3.email.server.tasks;

import org.prog3.email.AppendingObjectOutputStream;
import org.prog3.email.model.*;
import org.prog3.email.server.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

public abstract class ServerTask implements Runnable {
    String account;
    Model model;
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;

    public ServerTask(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }
    public ServerTask(String account, Model model, ObjectOutputStream out, ObjectInputStream in) {
        this.account = account;
        this.model = model;
        this.in = in;
        this.out = out;
    }

    private void openStreams() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void closeStreams() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        } catch (IOException e) {
            Logger.log(e.getMessage());
        }
    }
}

