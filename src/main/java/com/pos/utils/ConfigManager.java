package com.pos.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ConfigManager {
    private static final String RESOURCE_FILE = "config.properties";
    private static final Path OVERRIDE_FILE = Path.of("config.properties");
    private final Properties properties = new Properties();

    public ConfigManager() {
        load();
    }

    private void load() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(RESOURCE_FILE)) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException ignored) {
        }
        if (Files.exists(OVERRIDE_FILE)) {
            try (InputStream in = Files.newInputStream(OVERRIDE_FILE)) {
                properties.load(in);
            } catch (IOException ignored) {
            }
        }
    }

    public String get(String key, String fallback) {
        return properties.getProperty(key, fallback);
    }

    public double getDouble(String key, double fallback) {
        try {
            return Double.parseDouble(properties.getProperty(key, String.valueOf(fallback)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public void set(String key, String value) {
        properties.setProperty(key, value);
    }

    public void saveToProjectRoot() throws IOException {
        try (OutputStream out = Files.newOutputStream(OVERRIDE_FILE)) {
            properties.store(out, "POS runtime configuration");
        }
    }
}
