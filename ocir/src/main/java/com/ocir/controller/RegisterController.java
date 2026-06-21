package com.ocir.controller;

import com.ocir.App;
import com.ocir.dao.UserDAO;
import com.ocir.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm = confirmPasswordField.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Бүх талбарыг бөглөнө үү");
            return;
        }
        if (!password.equals(confirm)) {
            errorLabel.setText("Нууц үг таарахгүй байна");
            return;
        }
        User user = new User(username, email, password);
        if (userDAO.register(user)) {
            App.navigateTo("view/login.fxml");
        } else {
            errorLabel.setText("Бүртгэл амжилтгүй. Username эсвэл email давхцсан байна");
        }
    }

    @FXML
    private void goToLogin() {
        App.navigateTo("view/login.fxml");
    }
}
