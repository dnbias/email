package org.prog3.email.server.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.prog3.email.model.Email;
import org.util.logger.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class MakeJSON extends ServerTask {
    Email email;
    String account;
    File emailsDir;
    final Object lock;

    /*
     * Task to convert from Email to JSON
     */
    public MakeJSON(Email email, String account, File emailsDir, Object lock) {
        this.email = email;
        this.account = account;
        this.emailsDir = emailsDir;
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            String s = File.separator;
            File accountDir = new File("." + s + emailsDir.getName() + s +  account);
            File emailFile = new File( accountDir + s + email.getId() + ".json");

            synchronized (lock) { // synchronize in model's context
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
            }
            Logger.log("Written email: " + account + s + emailFile.getName());
        } catch (IOException e) {
            Logger.log(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
}
