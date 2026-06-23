package com.ocir;

import com.ocir.model.User;
import com.ocir.util.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static Stage primaryStage;
    private static User currentUser;
    private static User viewedUser;
    private static boolean darkMode = false;

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseManager.initDatabase();
        primaryStage = stage;
        primaryStage.setTitle("Ocir - Social Media");
        navigateTo("view/login.fxml");
        primaryStage.show();
    }

    public static void navigateTo(String fxml) {
        try {
            Parent root = FXMLLoader.load(App.class.getResource(fxml));
            Scene scene = new Scene(root);
            applyTheme(root);
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void applyTheme(Parent root) {
        root.getStylesheets().clear();
        if (darkMode) {
            root.getStylesheets().add(App.class.getResource("css/dark-style.css").toExternalForm());
        } else {
            root.getStylesheets().add(App.class.getResource("css/style.css").toExternalForm());
        }
    }

    public static boolean isDarkMode() { return darkMode; }
    public static void toggleDarkMode() {
        darkMode = !darkMode;
        if (primaryStage.getScene() != null && primaryStage.getScene().getRoot() != null) {
            applyTheme(primaryStage.getScene().getRoot());
        }
    }

    public static User getCurrentUser() { return currentUser; }
    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getViewedUser() { return viewedUser; }
    public static void setViewedUser(User user) { viewedUser = user; }

    public static void viewProfile(User user) {
        viewedUser = user;
        if (user.getId() == currentUser.getId()) {
            navigateTo("view/profile.fxml");
        } else {
            navigateTo("view/user_profile.fxml");
        }
    }

    public static void main(String[] args) { launch(args); }
}
