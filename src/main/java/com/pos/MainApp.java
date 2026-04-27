package com.pos;

import com.pos.utils.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        DatabaseInitializer.initialize();
        primaryStage = stage;
        loadScene("fxml/login.fxml", "POS Login", 980, 640);
        primaryStage.show();
    }

    public static void loadScene(String fxmlPath, String title, double width, double height) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getClassLoader().getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(MainApp.class.getClassLoader().getResource("css/styles.css").toExternalForm());
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.centerOnScreen();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
