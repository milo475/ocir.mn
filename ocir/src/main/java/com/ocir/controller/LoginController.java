package com.ocir.controller;

import com.ocir.App;
import com.ocir.dao.UserDAO;
import com.ocir.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Бүх талбарыг бөглөнө үү");
            return;
        }
        User user = userDAO.login(username, password);
        if (user != null) {
            App.setCurrentUser(user);
            App.navigateTo("view/main.fxml");
        } else {
            errorLabel.setText("Нэвтрэх нэр эсвэл нууц үг буруу");
        }
    }

    @FXML
    private void goToRegister() {
        App.navigateTo("view/register.fxml");
    }
}
