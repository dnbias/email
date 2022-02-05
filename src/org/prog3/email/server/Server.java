package org.prog3.email.server;

import org.prog3.email.Request;
import org.prog3.email.model.*;
import org.prog3.email.server.tasks.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Executors;
import java.util.*;

public class Server {
    private static Socket socket = null;
    private static final int NUM_THREADS = 10, PORT = 1025;
    private static ExecutorService executor = null;
    private static Vector<FutureTask<Void>> tasks = null;
    private static Model model;
    private static boolean running = true;

    public static void listen(int port){
        try {
            Logger.log("Listening on port " + port);
            ServerSocket serverSocket = new ServerSocket(port);
            executor = Executors.newFixedThreadPool(NUM_THREADS);
            tasks = new Vector<>();

            while (running) {
                serve(serverSocket);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Logger.log(e.getMessage());
        } finally {
            Logger.log("Shutting down server...");
            if (socket!=null)
                try {
                    socket.close();
                    Logger.log("Socket closed");
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.log(e.getMessage());
                }
        }
    }

    private static void serve(ServerSocket serverSocket){
        try {
            socket = serverSocket.accept();
            Logger.log(socket + " - Connection Established");
            Object request = ((ObjectInputStream) socket.getInputStream()).readObject();
            FutureTask<Void> ft = null;
            if (request instanceof Request r){
                switch (r.getType()) {
                    case PullMessages ->
                        ft = new FutureTask<>(
                               new SendMessageList(r.getAccount(), model, socket));

                    case PushMessage ->
                        ft = new FutureTask<>(
                                new SendMessage(r.getAccount(), r.getEmail(), model, socket));

                    case DeleteMessage ->
                         ft = new FutureTask<>(
                                 new DeleteMessage(r.getAccount(), r.getEmail(), model, socket));

                }
            } else {  // malformed request
                Logger.log(socket + " - Bad Request");
                ft = new FutureTask<>(
                        new NotifyBadRequest(socket));
            }
            assert ft != null;
            tasks.add(ft);
            executor.execute(ft);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Logger.log(e.getMessage());
        }
    }

    public static void main(String[] args) {

        model = new Model();
        if (args.length >= 0) // TODO debug flag
            model.init();

        Logger.log("Server Started");
        SetupShutdown();

        listen(PORT);
        Logger.log("Server Closed\n");
    }

    static void SetupShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
               Logger.log("ShutDown Hook");
               running = false;
        }));
    }
}
