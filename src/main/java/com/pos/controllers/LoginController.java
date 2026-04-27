package com.pos.controllers;

import com.pos.MainApp;
import com.pos.dao.UserDAO;
import com.pos.models.User;
import com.pos.utils.AlertHelper;
import com.pos.utils.SessionContext;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label statusLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void onLogin() {
        try {
            User user = userDAO.authenticate(usernameField.getText().trim(), passwordField.getText().trim());
            if (user == null) {
                statusLabel.setText("Invalid credentials.");
                return;
            }
            SessionContext.setCurrentUser(user);
            MainApp.loadScene("fxml/dashboard.fxml", "POS Dashboard", 1200, 760);
        } catch (Exception e) {
            AlertHelper.error("Login Error", "Failed to login: " + e.getMessage());
        }
    }
}
