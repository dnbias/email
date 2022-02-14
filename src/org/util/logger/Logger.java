package org.util.logger;

import javafx.application.Platform;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

public class Logger {
    static String path = "./server.log";
    static BufferedWriter writer = null;
    static Date date = new Date();
    static List<String> outList = null;

    public static void log(Socket s, String message) {
        Timestamp timestamp = new Timestamp(date.getTime());
        String socket = s.getRemoteSocketAddress().toString().replaceAll("/","");
        String messageFinal = timestamp + " | " + socket + " - " + message;

        print(messageFinal);
    }

    public static void log(String message) {
        Timestamp timestamp = new Timestamp(date.getTime());
        String messageFinal = timestamp + " | " + message;

        print(messageFinal);
    }

    private static synchronized void print(String message) {
        System.out.println(message); // write to stdout

        try { // write to file
            if (writer == null) {
                writer = new BufferedWriter(new FileWriter(path,true));
            }
            writer.append(message).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (outList != null) { // write to application
            Platform.runLater(() -> outList.add(message));
        }

        closeWriter();
    }

    public static void setOutputList(List<String> list) {
        outList = list;
    }

    public static void closeWriter() {
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
