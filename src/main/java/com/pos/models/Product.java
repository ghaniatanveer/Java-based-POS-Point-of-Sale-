package com.pos.models;

public class Product {
    private int id;
    private String name;
    private String barcode;
    private int categoryId;
    private double price;
    private int stockQty;

    public Product() {
    }

    public Product(int id, String name, String barcode, int categoryId, double price, int stockQty) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.categoryId = categoryId;
        this.price = price;
        this.stockQty = stockQty;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    @Override
    public String toString() {
        return name + " (" + barcode + ")";
    }
}
