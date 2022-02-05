package org.prog3.email.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.List;

public class Client extends Application {

    Socket socket = null;
    ObjectOutputStream outputStream = null;
    ObjectInputStream inputStream = null;
    int id;
    String account;
    final int MAX_ATTEMPTS = 5;

    public Client(int id, String account) {
        this.id = id;
        this.account = account;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("client.fxml"));
        primaryStage.setTitle("Email Client");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public void communicate(String host, int port){
        int attempts = 0;

        boolean success = false;
        while(attempts < MAX_ATTEMPTS && !success) {
            attempts += 1;
            System.out.println("[Client "+ this.id +"] Tentativo nr. " + attempts);

            success = tryCommunication(host, port);

            if(success) {
                continue;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean tryCommunication(String host, int port) {
        try {
            connectToServer(host, port);
            Request students = ;

            Thread.sleep(5000);

            sendStudents(students);
            receiveModifiedStudents();

            return true;
        } catch (ConnectException ce) {
            // nothing to be done
            return false;
        } catch (IOException | ClassNotFoundException se) {
            se.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeConnections();
        }
    }

    private void closeConnections() {
        if (socket != null) {
            try {
                inputStream.close();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendEmail(Email email) throws IOException {
        outputStream.writeObject(email);
        outputStream.flush();
    }

    private void receiveModifiedStudents() throws IOException, ClassNotFoundException {
        List<Student> modifiedStudents = (List<Student>) inputStream.readObject();
        System.out.println("[Client " + this.id + "] Ricevuti " + modifiedStudents.size() + " oggetti");

        if (modifiedStudents != null && modifiedStudents.size() > 0) {
            for (Student s : modifiedStudents) {
                System.out.println("[Client " + this.id + "] Oggetto ricevuto => " + s.toString());
            }
        }
    }

    private void connectToServer(String host, int port) throws IOException {
        socket = new Socket(host, port);
        outputStream = new ObjectOutputStream(socket.getOutputStream());

        // Dalla documentazione di ObjectOutputStream
        // callers may wish to flush the stream immediately to ensure that constructors for receiving
        // ObjectInputStreams will not block when reading the header.
        outputStream.flush();

        inputStream = new ObjectInputStream(socket.getInputStream());

        System.out.println("[Client "+ this.id + "] Connesso");
    }


    public static void main(String[] args) {
        launch(args);
    }
}
