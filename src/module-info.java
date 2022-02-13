module EmailClientServer {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires gson;
    requires org.jfxtras.styles.jmetro;
    requires java.sql;

    opens org.prog3.email.server to javafx.fxml, javafx.graphics;
    opens org.prog3.email.server.ui to javafx.fxml, javafx.graphics;
    opens org.prog3.email.model to javafx.base, javafx.fxml, javafx.graphics, gson;
    opens org.prog3.email.client.ui to javafx.fxml, javafx.graphics;
    opens org.prog3.email.client.model to javafx.fxml, javafx.graphics, gson;
    opens org.prog3.email.client.model.tasks to javafx.fxml, javafx.graphics;
}