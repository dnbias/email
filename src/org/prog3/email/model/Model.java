package org.prog3.email.model;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
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
        Logger.log("Model Initialized");
    }

    /*
     * Add account to connected list
     */
    public void addClient(String account, ObjectOutputStream out) {
        Platform.runLater(() -> connectedClientsMap.put(account, out));
        connectedClients.add(account);
    }

    /*
     * Remove account from connected list
     */
    public void removeClient(String account){
        connectedClientsMap.remove(account);
        connectedClients.remove(account);
    }

    public LinkedList<ObjectOutputStream> getConnectedOutputStreams(List<String> accounts) {
        LinkedList<ObjectOutputStream> r = new LinkedList<>();

        for (String account : accounts) {
            if (connectedClientsMap.containsKey(account)) {
                r.add(connectedClientsMap.get(account));
            }
        }

        return  r;
    }

    /*
     * Add account to the database
     */
    public void addAccount(String account) {
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
    public synchronized boolean deleteEmail(Email email) {
        boolean r = false;
        long time = email.getDate().getTime();
        String emailFilename = email.getSender() + File.separator + time + ".json";
        File[] foundFiles = emailsDir.listFiles((file,name) -> name.equals(emailFilename));

        if (foundFiles != null && foundFiles.length > 0) {
            for (File f : foundFiles) {
                r = f.delete();
            }
        }

        return r;
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

    // Used for initial database population or debugging
    public void initDebug() {
        addAccount("account@unito.it");
        addAccount("account1@unito.it");
        addAccount("account2@unito.it");

        Email email1 = new Email("account@unito.it", "account2@unito.it",
                "Test Email 1",
                "Dolore sit facilis ullam rerum quod ut nihil. Facere sint iste reiciendis commodi qui. Occaecati nihil ducimus est quaerat. Laboriosam itaque deleniti qui pariatur et. Voluptatem ut repellendus consequatur debitis voluptatem.\n" +
                "\n" +
                "Error perferendis debitis molestiae incidunt veritatis hic earum. Et facere velit sed quam omnis aut. Laboriosam explicabo nobis dolores qui.\n" +
                "\n" +
                "Soluta ut fugiat veritatis ducimus libero deleniti officia. Qui nesciunt nulla qui vitae dolorum sit sunt. Veritatis accusamus non commodi earum enim excepturi culpa. Laboriosam voluptatem aperiam ipsum aspernatur omnis. Quisquam quae est sapiente ut tempore ut ad.\n" +
                "\n" +
                "Consequatur omnis perspiciatis nihil voluptates itaque qui voluptatem quia. Dolore repellendus recusandae ex voluptatem repellat soluta sunt et. Molestias eius natus iusto totam perspiciatis aut. Et reprehenderit vero in esse nihil debitis.\n" +
                "\n" +
                "Accusantium magnam voluptas necessitatibus. Et quaerat et et eveniet. Ut est ab id omnis voluptatem tenetur qui. Architecto sit officiis non temporibus perspiciatis quia. Ipsa sapiente ea est dolores repellat sunt.\n",
                Calendar.getInstance().getTime());


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Email email2 = new Email("account1@unito.it", "account@unito.it",
                "Test Email 2",
                "Magni illo iste repellat soluta magnam non ut. Ipsam et in est sequi veniam animi labore. Omnis molestias id ducimus non vero voluptates autem. Est minus quasi enim. Voluptate ea veritatis omnis. Quae reprehenderit quia sit.\n" +
                "\n" +
                "Quis voluptate quasi in facere voluptatem. Facilis ea consequuntur totam quia dicta itaque sed. Odio nam modi incidunt.\n" +
                "\n" +
                "Laudantium blanditiis dolores explicabo magni mollitia. Corporis aut amet possimus voluptatem. Quisquam fuga assumenda harum sed. Et perferendis aut tenetur ut praesentium aut at eligendi.\n" +
                "\n" +
                "Magni est suscipit est dolores voluptatem accusantium quis. Omnis veritatis impedit eius nemo. Explicabo inventore quisquam nesciunt aliquam accusamus. Saepe facere laudantium vel quia perferendis ullam ut. Illum sit dolor magnam ad qui dolore quia. Accusamus non provident voluptas.\n" +
                "\n" +
                "Non laboriosam praesentium voluptates totam id quasi est similique. Facere reiciendis voluptatem non in molestiae qui optio rerum. Ut nihil dolorem et repellat quis.\n",
                Calendar.getInstance().getTime());
        addEmail(email1);
        addEmail(email2);

        Logger.log("Model initialized with test contents");
    }
}
