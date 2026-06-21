package com.ocir;

import com.ocir.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static Stage primaryStage;
    private static User currentUser;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Ocir - Social Media");
        navigateTo("view/login.fxml");
        primaryStage.show();
    }

    public static void navigateTo(String fxml) {
        try {
            Parent root = FXMLLoader.load(App.class.getResource(fxml));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static User getCurrentUser() { return currentUser; }
    public static void setCurrentUser(User user) { currentUser = user; }

    public static void main(String[] args) { launch(args); }
}
