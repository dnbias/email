package org.prog3.email.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.sql.Timestamp;

public class Logger {
    static String path = "./server.log";
    static BufferedWriter writer = null;

    public static void log(String message) {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String messageFinal = timestamp + " | " + message;
        System.out.println(messageFinal);
        try {
            if (writer == null) {
                writer = new BufferedWriter(new FileWriter(path));
            }
            writer.append(messageFinal);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
