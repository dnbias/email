package org.prog3.email.client.model.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.prog3.email.client.model.Configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class WriteConfiguration extends ClientTask {
    public WriteConfiguration() {}

    @Override
    public void run() {
        try {
            File conf = Configuration.path.toFile();

            synchronized (lock) { // synchronize in client's context
                conf.createNewFile();
                Writer writer = new FileWriter(conf);
                Gson gson = new GsonBuilder().create();
                gson.toJson(Configuration.instance, writer);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.readConfiguration();
    }
}
