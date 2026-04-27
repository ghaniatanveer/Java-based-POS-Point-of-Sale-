package com.pos.controllers;

import com.pos.dao.SettingsDAO;
import com.pos.utils.AlertHelper;
import com.pos.utils.ConfigManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.Map;

public class SettingsController {
    @FXML
    private TextField companyNameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField taxRateField;
    @FXML
    private TextField receiptFooterField;
    @FXML
    private TextField logoPathField;

    private final SettingsDAO settingsDAO = new SettingsDAO();
    private final ConfigManager config = new ConfigManager();

    @FXML
    protected void initialize() {
        try {
            Map<String, String> data = settingsDAO.getAll();
            companyNameField.setText(data.getOrDefault("company.name", config.get("company.name", "POS")));
            addressField.setText(data.getOrDefault("company.address", config.get("company.address", "")));
            phoneField.setText(data.getOrDefault("company.phone", config.get("company.phone", "")));
            taxRateField.setText(data.getOrDefault("tax.rate", config.get("tax.rate", "0.15")));
            receiptFooterField.setText(data.getOrDefault("receipt.footer", config.get("receipt.footer", "Thank you")));
            logoPathField.setText(data.getOrDefault("company.logo", config.get("company.logo", "src/main/resources/images/default_logo.png")));
        } catch (Exception e) {
            AlertHelper.error("Settings", "Failed to load settings.");
        }
    }

    @FXML
    private void onSave() {
        try {
            settingsDAO.upsert("company.name", companyNameField.getText().trim());
            settingsDAO.upsert("company.address", addressField.getText().trim());
            settingsDAO.upsert("company.phone", phoneField.getText().trim());
            settingsDAO.upsert("tax.rate", taxRateField.getText().trim());
            settingsDAO.upsert("receipt.footer", receiptFooterField.getText().trim());
            settingsDAO.upsert("company.logo", logoPathField.getText().trim());
            config.set("company.name", companyNameField.getText().trim());
            config.set("company.address", addressField.getText().trim());
            config.set("company.phone", phoneField.getText().trim());
            config.set("tax.rate", taxRateField.getText().trim());
            config.set("receipt.footer", receiptFooterField.getText().trim());
            config.set("company.logo", logoPathField.getText().trim());
            config.saveToProjectRoot();
            AlertHelper.info("Settings", "Branding and tax settings saved.");
        } catch (Exception e) {
            AlertHelper.error("Settings", "Failed to save settings.");
        }
    }
}
