package com.example.lab4;

public class Product {

    private int id;
    private String productName;
    private double price;

    public Product() {

    }

    public Product(int id, String productName, double price) {
        this.id = id;
        this.productName = productName;
        this.price = price;
    }

    public Product(String productName, double price) {
        productName = productName;
        price = price;
    }


    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }


}
