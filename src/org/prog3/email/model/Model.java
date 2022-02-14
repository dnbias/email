package org.prog3.email.model;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.prog3.email.server.tasks.MakeEmail;
import org.prog3.email.server.tasks.MakeJSON;
import org.prog3.email.server.tasks.ServerTask;
import org.util.logger.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

public class Model {
    File emailsDir;
    private static ThreadPoolExecutor executorImporting, executorExporting;
    public static int NUM_THREADS = 4;
    private static ObservableList<String> logContent, connectedClients;
    private static Map<String, ObjectOutputStream> connectedClientsMap;
    private static SimpleListProperty<String> log;
    private static SimpleListProperty<String> clients;
    private HashMap<String ,LinkedList<String>> pendingNotifications;

    public Model() {
        emailsDir = new File("Emails");
        if (!emailsDir.exists()) {
            emailsDir.mkdir();
        }
        executorImporting = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUM_THREADS/2);
        executorExporting = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUM_THREADS/2);

        logContent = FXCollections.observableList(new ArrayList<>());
        Logger.setOutputList(logContent);
        log = new SimpleListProperty<>();
        log.set(logContent);

        connectedClientsMap = new HashMap<>();
        connectedClients = FXCollections.observableList(new ArrayList<>());
        clients = new SimpleListProperty<>();
        clients.set(connectedClients);
        pendingNotifications = new HashMap<>();

        Logger.log("Model Initialized");
    }

    /*
     * Add account to connected list
     */
    public synchronized void addClient(String account, ObjectOutputStream out) {
        Platform.runLater(() -> connectedClientsMap.put(account, out));
        Platform.runLater(() -> connectedClients.add(account));
    }

    /*
     * Remove account from connected list
     */
    public synchronized void removeClient(String account){
        Platform.runLater(() -> connectedClientsMap.remove(account));
        Platform.runLater( () -> connectedClients.remove(account));
    }

    public LinkedList<ObjectOutputStream> getConnectedOutputStreams(List<String> accounts) {
        LinkedList<ObjectOutputStream> r = new LinkedList<>();

        for (String account : accounts) {
            if (connectedClientsMap.containsKey(account)) {
                Logger.log(account + " is connected");
                r.add(connectedClientsMap.get(account));
            }
        }

        return  r;
    }

    public LinkedList<String> getConnected(List<String> accounts) {
        LinkedList<String> r = new LinkedList<>();

        for (String account : accounts) {
            if (connectedClientsMap.containsKey(account)) {
                Logger.log(account + " is connected");
                r.add(account);
            }
        }

        return  r;
    }

    public LinkedList<String> getPendingNotifications(String account) {
        LinkedList<String> r = new LinkedList<>();
        if (pendingNotifications.containsKey(account)) {
            r = pendingNotifications.remove(account);
        }
        return  r;
    }

    /*
     * Add account to the database
     */
    public synchronized void addAccount(String account) {
        String path = "." + File.separator + emailsDir.getName() + File.separator + account;
        File fileAccount = new File(path);
        if (!fileAccount.mkdir()) {
            Logger.log("AddAccount - Account already exists: " + account);
        }
    }

    /*
     * Add email to database
     */
    public void addEmail(Email email) {
        makeJSON(email, email.getSender());
        for (String receiver : email.getReceivers()) {
            makeJSON(email, receiver);
        }
    }

    /*
     * Remove email from database
     */
    public synchronized boolean deleteEmail(String account, Email email) {
        boolean r = true;
        String emailFilename = email.getId() + ".json";
        File accountDir = new File(emailsDir + File.separator + account);
        Logger.log(accountDir + "  " +  emailFilename);
        File[] foundFiles = accountDir.listFiles((file,name) -> name.equals(emailFilename));

        if (foundFiles != null && foundFiles.length > 0) {
            for (File f : foundFiles) {
                r = r && f.delete();
                Logger.log("Deleted email: " + f.toString());
            }
        }

        return r;
    }

    /*
     * Adds notifications to send next client connection check
     */
    public void addPendingNotification(String account, String notification) {
        if (!pendingNotifications.containsKey(account)) {
            pendingNotifications.put(account, new LinkedList<>());
        }
        pendingNotifications.get(account).add(notification);
    }

    /*
     * Fetch emails in account's inbox
     */
    public ArrayList<Email> getEmails(String account){
        String path = "." + File.separator + emailsDir + File.separator + account;
        File accountDir = new File(path);
        Collection<FutureTask<Email>> tasks = new LinkedList<>();

        synchronized (this) {
            if (!accountDir.exists()) {
                accountDir.mkdir();
            }
        }

        ArrayList<Email> emails = new ArrayList<>();
        try {
            List<Path> files = Files.list(Path.of(path)).toList();
            for (Path f : files) {
                makeEmail(f, tasks);
            }
            for (FutureTask<Email> currentTask : tasks) { // Wait for the threads to finish
                Email currentResult = currentTask.get();
                if (currentResult != null) {
                    emails.add(currentResult);
                }
            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            Logger.log(e.getMessage());
            e.printStackTrace();
        }

        return emails;
    }

    public ListProperty<String> clientsProperty() {
        return clients;
    }

    public ListProperty<String> logProperty() {
        return log;
    }

    // Go from Json to Email
    private void makeEmail(Path filename, Collection<FutureTask<Email>> tasks) {
        MakeEmail task = new MakeEmail(filename, this);
        FutureTask<Email> future = new FutureTask<>(task);
        executorImporting.submit(future);
        tasks.add(future);
    }

    // Go from Email to Json
    private void makeJSON(Email email, String account) {
        ServerTask task = new MakeJSON(email, account, emailsDir,this);
        executorExporting.execute(task);
    }
}
