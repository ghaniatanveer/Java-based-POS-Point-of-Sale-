package com.pos.controllers;

import com.pos.dao.CategoryDAO;
import com.pos.dao.ProductDAO;
import com.pos.models.Category;
import com.pos.models.Product;
import com.pos.utils.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductController {
    @FXML
    private TableView<Product> table;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> barcodeColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Integer> stockColumn;
    @FXML
    private TextField nameField;
    @FXML
    private TextField barcodeField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField stockField;
    @FXML
    private ComboBox<Category> categoryCombo;
    @FXML
    private TextField searchField;

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        barcodeColumn.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQty"));
        loadCategories();
        loadProducts();
    }

    @FXML
    private void onSave() {
        try {
            Product p = new Product();
            p.setName(nameField.getText().trim());
            p.setBarcode(barcodeField.getText().trim());
            p.setCategoryId(categoryCombo.getValue().getId());
            p.setPrice(Double.parseDouble(priceField.getText().trim()));
            p.setStockQty(Integer.parseInt(stockField.getText().trim()));
            productDAO.save(p);
            clearForm();
            loadProducts();
        } catch (Exception e) {
            AlertHelper.error("Product", "Unable to save product.");
        }
    }

    @FXML
    private void onUpdate() {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.info("Product", "Select a product first.");
            return;
        }
        try {
            selected.setName(nameField.getText().trim());
            selected.setBarcode(barcodeField.getText().trim());
            selected.setCategoryId(categoryCombo.getValue().getId());
            selected.setPrice(Double.parseDouble(priceField.getText().trim()));
            selected.setStockQty(Integer.parseInt(stockField.getText().trim()));
            productDAO.update(selected);
            loadProducts();
        } catch (Exception e) {
            AlertHelper.error("Product", "Unable to update product.");
        }
    }

    @FXML
    private void onDelete() {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        try {
            productDAO.delete(selected.getId());
            loadProducts();
        } catch (Exception e) {
            AlertHelper.error("Product", "Unable to delete product.");
        }
    }

    @FXML
    private void onSearch() {
        try {
            table.setItems(FXCollections.observableArrayList(productDAO.search(searchField.getText().trim())));
        } catch (Exception e) {
            AlertHelper.error("Product", "Search failed.");
        }
    }

    @FXML
    private void onTableSelect() {
        Product p = table.getSelectionModel().getSelectedItem();
        if (p == null) {
            return;
        }
        nameField.setText(p.getName());
        barcodeField.setText(p.getBarcode());
        priceField.setText(String.valueOf(p.getPrice()));
        stockField.setText(String.valueOf(p.getStockQty()));
        categoryCombo.getItems().stream().filter(c -> c.getId() == p.getCategoryId()).findFirst().ifPresent(categoryCombo::setValue);
    }

    private void loadProducts() {
        try {
            table.setItems(FXCollections.observableArrayList(productDAO.findAll()));
        } catch (Exception e) {
            AlertHelper.error("Product", "Failed to load products.");
        }
    }

    private void loadCategories() {
        try {
            categoryCombo.setItems(FXCollections.observableArrayList(categoryDAO.findAll()));
            if (!categoryCombo.getItems().isEmpty()) {
                categoryCombo.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            AlertHelper.error("Category", "Failed to load categories.");
        }
    }

    private void clearForm() {
        nameField.clear();
        barcodeField.clear();
        priceField.clear();
        stockField.clear();
    }
}
