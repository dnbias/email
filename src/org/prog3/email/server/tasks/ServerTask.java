package org.prog3.email.server.tasks;

import org.prog3.email.model.*;
import org.prog3.email.server.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

abstract class ServerTask implements Callable<Void> {
    String account;
    Model model;
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;

    public ServerTask(Socket socket) {
        this.socket = socket;
        openStreams();
    }
    public ServerTask(String account, Model model, Socket socket) {
        this.account = account;
        this.model = model;
        this.socket = socket;
        openStreams();
    }

    private void openStreams() {
        try {
            this.in = (ObjectInputStream) socket.getInputStream();
            this.out = (ObjectOutputStream) socket.getOutputStream();
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

