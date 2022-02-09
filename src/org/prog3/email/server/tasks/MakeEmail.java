package org.prog3.email.server.tasks;


import org.prog3.email.model.Email;
import org.util.logger.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public class MakeEmail implements Callable<Email> {
    final Object lock;
    Path filename;

    /*
     * Task to convert from JSON to Email and return it
     */
    public MakeEmail(Path filename, Object lock) {
        this.filename = filename;
        this.lock = lock;
    }

    @Override
    public Email call() {
        Email email = null;
        synchronized (lock) { // synchronize in model's context
            try (Reader reader = new FileReader(filename.toString())) {
                Gson gson = new GsonBuilder().create();
                email = gson.fromJson(reader, Email.class);
            } catch (IOException e) {
                Logger.log(e.getMessage());
                e.printStackTrace();
            }
        }
        return email;
    }
}
