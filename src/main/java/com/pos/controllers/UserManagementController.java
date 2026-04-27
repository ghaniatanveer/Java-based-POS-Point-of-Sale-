package com.pos.controllers;

import com.pos.dao.UserDAO;
import com.pos.models.User;
import com.pos.utils.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserManagementController {
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> roleColumn;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        try {
            userTable.setItems(FXCollections.observableArrayList(userDAO.findAll()));
        } catch (Exception e) {
            AlertHelper.error("Users", "Failed to load users.");
        }
    }
}
