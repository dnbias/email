package org.prog3.email.client.model.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.prog3.email.client.model.Configuration;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

public class ReadConfiguration extends ClientTask {
    Path conf;
    public ReadConfiguration(Path conf) {
        this.conf = conf;
    }

    @Override
    public void run() {
        Configuration configuration = null;
        try (Reader reader = new FileReader(conf.toString())) {
            Gson gson = new GsonBuilder().create();
            configuration = gson.fromJson(reader, Configuration.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Configuration.instance = configuration;
        client.readConfiguration();
    }
}
