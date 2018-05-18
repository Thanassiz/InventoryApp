package com.example.android.inventoryapp;

import com.example.android.inventoryapp.data.Shirt;

/**
 * Created by Thanassis on 26/4/2018.
 * MVVM for Shirt
 */

public class ShirtViewModel extends Shirt {

    public boolean isChecked = false;

    public ShirtViewModel(String name, byte[] images, float price, int size, int quantity, String supplierName, String supplierPhoneNumber) {
        super(name, images, price, size, quantity, supplierName, supplierPhoneNumber);
    }

    public ShirtViewModel(int id, String name, byte[] images, float price, int size, int quantity, String supplierName, String supplierPhoneNumber) {
        super(id, name, images, price, size, quantity, supplierName, supplierPhoneNumber);
    }
}
