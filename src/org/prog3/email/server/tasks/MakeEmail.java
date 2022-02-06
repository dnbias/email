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
import java.util.concurrent.FutureTask;

public class MakeEmail implements Callable<Email> {
    final Object lock;
    Path filename;

    public MakeEmail(Path filename, Object lock) {
        this.filename = filename;
        this.lock = lock;
    }

    @Override
    public Email call() {
        Email email = null;
        synchronized (lock) {
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
