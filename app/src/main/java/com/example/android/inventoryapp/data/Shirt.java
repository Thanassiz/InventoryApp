package com.example.android.inventoryapp.data;

/**
 * Created by Thanassis on 26/4/2018.
 * Main Shirt Model for DB
 */

public class Shirt {

    private int id = -1;
    private String name;
    private byte[] images;
    private float price;
    private int size;
    private int quantity;
    private String supplierName;
    private String supplierPhoneNumber;

    public Shirt(String name,byte[] images, float price, int size, int quantity, String supplierName, String supplierPhoneNumber) {
        this.name = name;
        this.images = images;
        this.price = price;
        this.size = size;
        this.quantity = quantity;
        this.supplierName = supplierName;
        this.supplierPhoneNumber = supplierPhoneNumber;
    }
    public Shirt(int id, String name,byte[] images, float price, int size, int quantity, String supplierName, String supplierPhoneNumber) {
        this.id = id;
        this.name = name;
        this.images = images;
        this.price = price;
        this.size = size;
        this.quantity = quantity;
        this.supplierName = supplierName;
        this.supplierPhoneNumber = supplierPhoneNumber;
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

    public byte[] getImages() {
        return images;
    }

    public void setImages(byte[] images) {
        this.images = images;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierPhoneNumber() {
        return supplierPhoneNumber;
    }

    public void setSupplierPhoneNumber(String supplierPhoneNumber) {
        this.supplierPhoneNumber = supplierPhoneNumber;
    }
}
