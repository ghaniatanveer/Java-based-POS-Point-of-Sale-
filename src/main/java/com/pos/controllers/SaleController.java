package com.pos.controllers;

import com.pos.dao.ProductDAO;
import com.pos.dao.SaleDAO;
import com.pos.dao.SettingsDAO;
import com.pos.models.Product;
import com.pos.models.Sale;
import com.pos.models.SaleItem;
import com.pos.utils.AlertHelper;
import com.pos.MainApp;
import com.pos.utils.PDFReceiptGenerator;
import com.pos.utils.SessionContext;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SaleController {
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<Product> productCombo;
    @FXML
    private TextField qtyField;
    @FXML
    private TextField discountField;
    @FXML
    private TableView<SaleItem> cartTable;
    @FXML
    private TableColumn<SaleItem, String> itemNameColumn;
    @FXML
    private TableColumn<SaleItem, Integer> itemQtyColumn;
    @FXML
    private TableColumn<SaleItem, Double> itemPriceColumn;
    @FXML
    private TableColumn<SaleItem, Double> itemTotalColumn;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label taxLabel;
    @FXML
    private Label grandTotalLabel;
    @FXML
    private Button checkoutButton;

    private final ProductDAO productDAO = new ProductDAO();
    private final SaleDAO saleDAO = new SaleDAO();
    private final SettingsDAO settingsDAO = new SettingsDAO();
    private final List<SaleItem> cart = new java.util.ArrayList<>();
    private final AtomicBoolean checkoutBusy = new AtomicBoolean(false);

    @FXML
    private void initialize() {
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        itemQtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        itemTotalColumn.setCellValueFactory(new PropertyValueFactory<>("lineTotal"));
        qtyField.setText("1");
        discountField.setText("0");
        refreshProducts();
        refreshTotals();
    }

    @FXML
    private void onSearchProduct() {
        try {
            productCombo.setItems(FXCollections.observableArrayList(productDAO.search(searchField.getText().trim())));
            if (!productCombo.getItems().isEmpty()) {
                productCombo.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            AlertHelper.error("Sale", "Product search failed.");
        }
    }

    @FXML
    private void onAddItem() {
        Product p = productCombo.getValue();
        if (p == null) {
            return;
        }
        try {
            int qty = Integer.parseInt(qtyField.getText().trim());
            if (qty <= 0) {
                return;
            }
            cart.add(new SaleItem(p.getId(), p.getName(), qty, p.getPrice()));
            cartTable.setItems(FXCollections.observableArrayList(cart));
            refreshTotals();
        } catch (NumberFormatException e) {
            AlertHelper.error("Sale", "Quantity must be a number.");
        }
    }

    @FXML
    private void onCheckout() {
        if (cart.isEmpty()) {
            AlertHelper.info("Sale", "Cart is empty.");
            return;
        }
        if (!checkoutBusy.compareAndSet(false, true)) {
            return;
        }
        checkoutButton.setDisable(true);
        try {
            Map<String, String> settings = settingsDAO.getAll();
            double taxRate = Double.parseDouble(settings.getOrDefault("tax.rate", "0.15"));
            double subtotal = cart.stream().mapToDouble(SaleItem::getLineTotal).sum();
            double discount = Double.parseDouble(discountField.getText().trim());
            double tax = Math.max(0, (subtotal - discount) * taxRate);
            double total = subtotal - discount + tax;

            Sale sale = new Sale();
            sale.setCashierUserId(SessionContext.getCurrentUser().getId());
            sale.setCreatedAt(LocalDateTime.now());
            sale.setItems(new java.util.ArrayList<>(cart));
            sale.setSubtotal(subtotal);
            sale.setDiscount(discount);
            sale.setTax(tax);
            sale.setTotal(total);
            int saleId = saleDAO.save(sale);
            sale.setId(saleId);

            Path receiptPath = promptSaveReceiptPdf(sale, settings);
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(receiptPath.toFile());
                } catch (Exception ignored) {
                }
            }
            AlertHelper.info("Sale Complete", "Sale saved.\nReceipt PDF:\n" + receiptPath.toAbsolutePath());
            cart.clear();
            cartTable.getItems().clear();
            refreshTotals();
            refreshProducts();
        } catch (Exception e) {
            AlertHelper.error("Sale", "Checkout failed: " + e.getMessage());
        } finally {
            checkoutBusy.set(false);
            checkoutButton.setDisable(false);
        }
    }

    private Path promptSaveReceiptPdf(Sale sale, Map<String, String> settings) throws Exception {
        String company = settings.getOrDefault("company.name", "POS");
        String footer = settings.getOrDefault("receipt.footer", "Thank you");
        String defaultName = "receipt-" + sale.getId() + ".pdf";

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Receipt PDF");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        chooser.setInitialFileName(defaultName);

        Path downloads = Path.of(System.getProperty("user.home"), "Downloads");
        if (Files.isDirectory(downloads)) {
            chooser.setInitialDirectory(downloads.toFile());
        }

        var saveFile = chooser.showSaveDialog(MainApp.getPrimaryStage());
        if (saveFile == null) {
            return PDFReceiptGenerator.generate(sale, company, footer);
        }
        Path outputPath = saveFile.toPath();
        if (!outputPath.toString().toLowerCase().endsWith(".pdf")) {
            outputPath = Path.of(outputPath.toString() + ".pdf");
        }
        return PDFReceiptGenerator.generateTo(outputPath, sale, company, footer);
    }

    private void refreshProducts() {
        try {
            productCombo.setItems(FXCollections.observableArrayList(productDAO.findAll()));
            if (!productCombo.getItems().isEmpty()) {
                productCombo.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            AlertHelper.error("Sale", "Failed to load products.");
        }
    }

    private void refreshTotals() {
        double subtotal = cart.stream().mapToDouble(SaleItem::getLineTotal).sum();
        double discount = 0;
        try {
            discount = Double.parseDouble(discountField.getText().trim());
        } catch (Exception ignored) {
        }
        double taxRate = 0.15;
        double tax = Math.max(0, (subtotal - discount) * taxRate);
        double total = subtotal - discount + tax;
        subtotalLabel.setText(String.format("Subtotal: %.2f", subtotal));
        taxLabel.setText(String.format("Tax: %.2f", tax));
        grandTotalLabel.setText(String.format("Total: %.2f", total));
    }
}
