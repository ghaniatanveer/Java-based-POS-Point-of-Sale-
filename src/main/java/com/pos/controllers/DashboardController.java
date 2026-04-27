package com.pos.controllers;

import com.pos.dao.ProductDAO;
import com.pos.dao.SaleDAO;
import com.pos.dao.SettingsDAO;
import com.pos.models.Product;
import com.pos.models.Sale;
import com.pos.utils.AlertHelper;
import com.pos.utils.SalesReportPDFGenerator;
import com.pos.utils.SessionContext;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DashboardController {
    @FXML
    private Label companyLabel;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label lowStockLabel;
    @FXML
    private Label totalSalesLabel;
    @FXML
    private DatePicker fromDate;
    @FXML
    private DatePicker toDate;
    @FXML
    private TableView<Sale> salesTable;
    @FXML
    private TableColumn<Sale, Integer> saleIdColumn;
    @FXML
    private TableColumn<Sale, String> saleDateColumn;
    @FXML
    private TableColumn<Sale, Double> totalColumn;
    private List<Sale> currentFilteredSales = java.util.Collections.emptyList();

    private final ProductDAO productDAO = new ProductDAO();
    private final SaleDAO saleDAO = new SaleDAO();
    private final SettingsDAO settingsDAO = new SettingsDAO();

    @FXML
    private void initialize() {
        saleIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        saleDateColumn.setCellValueFactory(cellData -> {
            Sale s = cellData.getValue();
            if (s == null || s.getCreatedAt() == null) {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty(s.getCreatedAt().toString().replace("T", " "));
        });
        attachSalesRowContextMenu();
        fromDate.setValue(LocalDate.now().minusDays(7));
        toDate.setValue(LocalDate.now());
        refreshSummary();
        salesFilter();
        Platform.runLater(() -> companyLabel.getScene().addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.B) {
                openWindow("fxml/branding-wizard.fxml", "Branding Wizard");
            }
        }));
    }

    private void attachSalesRowContextMenu() {
        salesTable.setRowFactory(tv -> {
            TableRow<Sale> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Delete sale (restore stock)");
            deleteItem.setOnAction(event -> {
                Sale sale = row.getItem();
                if (sale != null) {
                    confirmDeleteSale(sale);
                }
            });
            menu.getItems().add(deleteItem);
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem == null) {
                    row.setContextMenu(null);
                    return;
                }
                boolean admin = SessionContext.getCurrentUser() != null
                        && "ADMIN".equalsIgnoreCase(SessionContext.getCurrentUser().getRole());
                row.setContextMenu(admin ? menu : null);
            });
            return row;
        });
    }

    private void confirmDeleteSale(Sale sale) {
        if (SessionContext.getCurrentUser() == null
                || !"ADMIN".equalsIgnoreCase(SessionContext.getCurrentUser().getRole())) {
            AlertHelper.info("Sales", "Only Admin can delete a sale.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete sale");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete sale #" + sale.getId() + " and restore stock to inventory?\nThis cannot be undone.");
        var result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }
        try {
            saleDAO.deleteSaleAndRestoreStock(sale.getId());
            salesFilter();
            refreshSummary();
            AlertHelper.info("Sales", "Sale #" + sale.getId() + " deleted. Stock restored.");
        } catch (Exception e) {
            AlertHelper.error("Sales", "Could not delete sale.");
        }
    }

    @FXML
    private void openProducts() {
        openWindow("fxml/products.fxml", "Product Management");
    }

    @FXML
    private void openSales() {
        openWindow("fxml/sale.fxml", "New Sale");
    }

    @FXML
    private void openCustomers() {
        openWindow("fxml/customers.fxml", "Customers");
    }

    @FXML
    private void openSettings() {
        openWindow("fxml/settings.fxml", "Settings");
    }

    @FXML
    private void salesFilter() {
        try {
            List<Sale> sales = saleDAO.findByDateRange(fromDate.getValue(), toDate.getValue());
            currentFilteredSales = sales;
            salesTable.setItems(FXCollections.observableArrayList(sales));
            double sum = sales.stream().mapToDouble(Sale::getTotal).sum();
            totalSalesLabel.setText(String.format("Total Sales (range, PKR): %.2f", sum));
        } catch (Exception e) {
            // Keep dashboard usable even if history query fails.
            currentFilteredSales = java.util.Collections.emptyList();
            salesTable.setItems(FXCollections.observableArrayList());
            totalSalesLabel.setText("Total Sales (range, PKR): 0.00");
        }
    }

    @FXML
    private void setTodayRange() {
        LocalDate today = LocalDate.now();
        fromDate.setValue(today);
        toDate.setValue(today);
        salesFilter();
    }

    @FXML
    private void setMonthlyRange() {
        LocalDate today = LocalDate.now();
        fromDate.setValue(today.withDayOfMonth(1));
        toDate.setValue(today);
        salesFilter();
    }

    @FXML
    private void setYearlyRange() {
        LocalDate today = LocalDate.now();
        fromDate.setValue(today.withDayOfYear(1));
        toDate.setValue(today);
        salesFilter();
    }

    @FXML
    private void printSelectedReport() {
        try {
            LocalDate from = fromDate.getValue();
            LocalDate to = toDate.getValue();
            if (from == null || to == null) {
                AlertHelper.info("Print Report", "Please select date range first.");
                return;
            }
            String label = "Custom";
            if (from.equals(to) && from.equals(LocalDate.now())) {
                label = "Today";
            } else if (from.equals(LocalDate.now().withDayOfMonth(1)) && to.equals(LocalDate.now())) {
                label = "Monthly";
            } else if (from.equals(LocalDate.now().withDayOfYear(1)) && to.equals(LocalDate.now())) {
                label = "Yearly";
            }
            Map<String, String> settings = settingsDAO.getAll();
            String defaultName = String.format("sales-report-%s-%s-to-%s.pdf",
                    label.toLowerCase(),
                    from,
                    to);
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Sales Report PDF");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            chooser.setInitialFileName(defaultName);

            Path downloads = Path.of(System.getProperty("user.home"), "Downloads");
            if (Files.isDirectory(downloads)) {
                chooser.setInitialDirectory(downloads.toFile());
            }

            var saveFile = chooser.showSaveDialog(com.pos.MainApp.getPrimaryStage());
            if (saveFile == null) {
                return;
            }

            Path outputPath = saveFile.toPath();
            if (!outputPath.toString().toLowerCase().endsWith(".pdf")) {
                outputPath = Path.of(outputPath.toString() + ".pdf");
            }

            SalesReportPDFGenerator.generateTo(
                    outputPath,
                    label,
                    from,
                    to,
                    currentFilteredSales,
                    settings.getOrDefault("company.name", "POS"));

            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(outputPath.toFile());
                } catch (Exception ignored) {
                }
            }

            AlertHelper.info("Print Report", "PDF saved to:\n" + outputPath.toAbsolutePath());
        } catch (Exception e) {
            AlertHelper.error("Print Report", "Unable to generate report PDF.");
        }
    }

    private void refreshSummary() {
        try {
            Map<String, String> settings = settingsDAO.getAll();
            companyLabel.setText(settings.getOrDefault("company.name", "POS"));
            welcomeLabel.setText("Welcome, " + SessionContext.getCurrentUser().getUsername() + " (" + SessionContext.getCurrentUser().getRole() + ")");
            long low = productDAO.findAll().stream().filter(p -> p.getStockQty() <= 5).count();
            lowStockLabel.setText("Low Stock Alerts: " + low);
        } catch (Exception e) {
            companyLabel.setText("POS");
        }
    }

    private void openWindow(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root, 1000, 650));
            stage.showAndWait();
            refreshSummary();
            salesFilter();
        } catch (Exception e) {
            AlertHelper.error("Navigation Error", "Unable to open screen: " + e.getMessage());
        }
    }
}
