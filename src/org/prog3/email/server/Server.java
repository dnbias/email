package org.prog3.email.server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.prog3.email.model.Request;
import org.prog3.email.model.RequestType;
import org.prog3.email.model.*;
import org.prog3.email.server.ui.*;
import org.prog3.email.server.tasks.*;
import org.util.logger.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

public class Server extends Application {
    private static Socket socket = null;
    private static ServerSocket serverSocket;
    private static final int NUM_THREADS = 10, PORT = 8888;
    private static ThreadPoolExecutor executor = null;
    private static Vector<ServerTask> tasks = null;
    private static Model model;
    private static ServerController controller;

    public Server() {
        model = new Model();
        controller = new ServerController();
        ServerTask.initialize(model);
    }


    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(true); // really close
        Logger.log("Server Started");
        SetupShutdown();

        URL url = getClass().getResource("/server.fxml");

        assert url != null;

        FXMLLoader loader = new FXMLLoader(url);
        loader.setController(controller);
        stage.setTitle("Email Server");
        stage = loader.load();
        stage.setHeight(800);
        stage.setWidth(550);
        stage.getIcons().add(
                new Image(Server.class.getResourceAsStream("/server.png")));
        //stage.initStyle(StageStyle.UNDECORATED);


        // sleek dark style for FXML
        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(stage.getScene());

        controller.initialize(model);
        stage.show();
        stage.setOnCloseRequest(e -> System.exit(0)); // actually close
        new Thread( () -> listen(PORT) ).start(); // start main loop

        Logger.log("Server Initialized");
    }

    /*
     * Main Loop of the server
     * @param int port of the connection
     */
    public static void listen(int port){
        try {
            Logger.log("Listening on port " + port);
            serverSocket = new ServerSocket(port);
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUM_THREADS);
            tasks = new Vector<>();

            while (true) { // main loop
                socket = serverSocket.accept();
                executor.execute(() -> serve(socket));
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

    // Serve the newly connected client on the socket
    private static void serve(Socket socket){
        Logger.log(socket,"Connection Established");

        try {
            // OOS has to be created before OIS to write its header
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            waitForIdentification(socket, out, in);
            waitForRequests(socket, out,in);

        } catch (SocketException s) {
            Logger.log(socket, "Disconnected");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Logger.log(e.getMessage());
        }
    }

    // First thing for a new socket, identify the account
    private static void waitForIdentification(Socket socket, ObjectOutputStream out, ObjectInputStream in)
            throws ClassNotFoundException, IOException {
        boolean identified = false;
        while (!identified) {
            Object request = in.readObject(); // wait for request

            ServerTask task;
            if (request instanceof Request r && r.getType() == RequestType.Identification) {
                Logger.log(socket, r.getType() + " [" + r.getAccount() + "]");
                task = new IdentifyConnection(out, in, r.getAccount());
                identified = true;
            } else {  // malformed request
                Logger.log(socket, "Bad Request");
                task = new NotifyClient(out, in, "Error: ID Connection");
            }
            tasks.add(task);
            executor.execute(task); // serve request
        }
    }

    // After the verification of the account, wait for the requests and serve them
    private static void waitForRequests(Socket socket, ObjectOutputStream out, ObjectInputStream in)
            throws ClassNotFoundException, IOException {
        boolean openConnection = true;
        LinkedList<NotifyClient> notifications = new LinkedList<>(); // tasks to notify connected clients

        while (openConnection) {
            Object request = in.readObject(); // wait for request

            ServerTask task = null;
            if (notifications.size() > 0) {
                notifications.clear();
            }

            if (request instanceof Request r) {
                if (r.getType() != RequestType.CheckNotifications) {
                    Logger.log(socket, r.getType() + " [" + r.getAccount() + "]");
                }
                switch (r.getType()) {
                    case PullMessages -> task = new SendMessageList(r.getAccount(), out, in);

                    case PushMessage -> task = new SendEmail(r.getAccount(), r.getEmail(), out, in);

                    case DeleteMessage -> task = new DeleteEmail(r.getAccount(), r.getEmail(), out, in);

                    case CheckNotifications -> task = new PullPendingNotifications(r.getAccount(), out, in);

                    case CloseConnection -> {
                        task = new CloseConnection(socket, r.getAccount(), out, in);
                        openConnection = false;
                    }
                }
            } else {  // malformed request
                Logger.log(socket, "Bad Request");
                task = new NotifyClient(out, in);
            }
            assert task != null;

            tasks.add(task);
            executor.execute(task); // serve request
            for (NotifyClient notification : notifications) {
                executor.execute(notification);
            }
        }
    }

    public static void main(String[] args) { launch(args); }

    // Shutdown hook for closing the sockets if any are still open
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
