package org.prog3.email.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.prog3.email.server.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Model {
    File emailsDir = null;

    public Model() {
        emailsDir = new File("Emails");
        if (!emailsDir.exists()) {
            emailsDir.mkdir();
        }
    }

    public void addAccount(String account) {
        String path = "." + File.separator + emailsDir.getName() + File.separator + account;
        File fileAccount = new File(path);
        if (!fileAccount.mkdir()) {
            Logger.log("AddAccount - Account already exists: " + account);
        }
    }

    public void addEmail(Email email) {
        makeJSON(email, email.getSender());
        for (String receiver : email.getReceivers()) {
            makeJSON(email, receiver);
        }
    }

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

    public synchronized ArrayList<Email> getEmails(String account){
        String path = "." + File.separator + emailsDir + File.separator + account;
        File accountDir = new File(path);
        if (!accountDir.exists()) {
            accountDir.mkdir();
        }
        ArrayList<Email> emails = new ArrayList<>();
        try {
            List<Path> files = Files.list(Path.of(path)).toList();
            for (Path f : files) {
                emails.add(makeEmail(f));
            }
        } catch (IOException e) {
            Logger.log(e.getMessage());
            e.printStackTrace();
        }

        return emails;
    }

    private Email makeEmail(Path filename) {
        Email email = null;
        try (Reader reader = new FileReader(filename.toString())) {
            Gson gson = new GsonBuilder().create();
            email = gson.fromJson(reader, Email.class);
        } catch (IOException e) {
            Logger.log(e.getMessage());
            e.printStackTrace();
        }
        return email;
    }

    private void makeJSON(Email email, String account) {
        try {
            String s = File.separator;
            long time = email.getDate().getTime();
            File accountDir = new File("." + s + emailsDir.getName() + s +  account);
            File emailFile = new File( accountDir + s + time + ".json");
            if (!accountDir.exists()) {
                accountDir.mkdir();
            }
            if (!emailFile.createNewFile()) {
                Logger.log("Email " + account + s + emailFile.getName() + " already exists");
                return;
            }
            Writer writer = new FileWriter(emailFile);
            Gson gson = new GsonBuilder().create();
            gson.toJson(email, writer);
            writer.flush();
            writer.close();
            Logger.log("Written email: " + account + s + emailFile.getName());
        } catch (IOException e) {
            Logger.log(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void init() {
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
