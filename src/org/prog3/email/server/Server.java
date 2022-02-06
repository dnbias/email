package org.prog3.email.server;

import org.prog3.email.Request;
import org.prog3.email.model.*;
import org.prog3.email.server.tasks.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    private static Socket socket = null;
    private static ServerSocket serverSocket;
    private static final int NUM_THREADS = 10, PORT = 8888;
    private static ThreadPoolExecutor executor = null;
    private static Vector<ServerTask> tasks = null;
    private static Model model;
    private static boolean running = true;

    public static void listen(int port){
        try {
            Logger.log("Listening on port " + port);
            serverSocket = new ServerSocket(port);
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUM_THREADS);
            tasks = new Vector<>();

            while (running) {
                serve(serverSocket);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Logger.log(e.getMessage());
        } finally {
            Logger.log("Shutting down server...");
            if (socket!=null) {
                try {
                    socket.close();
                    Logger.log("Socket closed");
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.log(e.getMessage());
                }
            }
        }
    }

    private static void serve(ServerSocket serverSocket){
        try {
            socket = serverSocket.accept();
            Logger.log(socket + " - Connection Established");
            // OOS has to be created before OIS to write its header
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Object request = in.readObject(); // wait for request

            ServerTask task = null;
            if (request instanceof Request r){
                Logger.log(socket + " - " + r.getType() + " ["+  r.getAccount()  +"]");
                switch (r.getType()) {
                    case PullMessages ->
                        task = new SendMessageList(r.getAccount(), model, out, in);

                    case PushMessage ->
                        task = new SendMessage(r.getAccount(), r.getEmail(), model, out, in);

                    case DeleteMessage ->
                         task = new DeleteMessage(r.getAccount(), r.getEmail(), model, out, in);

                }
            } else {  // malformed request
                Logger.log(socket + " - Bad Request");
                task = new NotifyBadRequest(out, in);
            }
            assert task != null;
            tasks.add(task);
            executor.execute(task);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Logger.log(e.getMessage());
        }
    }

    public static void main(String[] args) {

        model = new Model();
        model.init();

        Logger.log("Server Started");
        SetupShutdown();

        listen(PORT);
        Logger.log("Server Closed\n");
    }

    static void SetupShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.log("ShutDown Hook");
            try {
                if (socket != null)
                    socket.close();
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}
