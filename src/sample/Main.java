package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main extends Application {
    public static Logger logger = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Сборщик");
        primaryStage.setScene(new Scene(root, 769, 535));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException {
        /*String localDate = LocalDateTime.now().toString();
        localDate = localDate.substring(0, localDate.indexOf("."))
                .replace(":", "-")
                .replace("T", "_");*/
        String fileLogsName = "Logs.log";

        FileHandler fh = new FileHandler(fileLogsName);
        logger.addHandler(fh);
        logger.setLevel(Level.ALL);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        launch(args);
    }
}
