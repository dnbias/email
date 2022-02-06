package org.util.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.sql.Timestamp;

public class Logger {
    static String path = "./server.log", messageFinal;
    static BufferedWriter writer = null;
    static Date date = new Date();

    public static void log(String message) {
        Timestamp timestamp = new Timestamp(date.getTime());
        messageFinal = timestamp + " | " + message;
        System.out.println(messageFinal);
        try {
            if (writer == null) {
                writer = new BufferedWriter(new FileWriter(path,true));
            }
            writer.append(messageFinal).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        close();
    }

    public static void close() {
        try {
            if (writer != null) {
                writer.flush();
                writer.close();
                writer = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
