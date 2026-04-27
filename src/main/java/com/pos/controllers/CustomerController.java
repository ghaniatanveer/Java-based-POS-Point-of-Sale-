package com.pos.controllers;

import com.pos.dao.CustomerDAO;
import com.pos.models.Customer;
import com.pos.utils.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class CustomerController {
    @FXML
    private TableView<Customer> table;
    @FXML
    private TableColumn<Customer, Integer> idColumn;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private TableColumn<Customer, String> phoneColumn;
    @FXML
    private TableColumn<Customer, String> emailColumn;

    private final CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        load();
    }

    private void load() {
        try {
            table.setItems(FXCollections.observableArrayList(customerDAO.findAll()));
        } catch (Exception e) {
            AlertHelper.error("Customers", "Failed loading customers.");
        }
    }
}
