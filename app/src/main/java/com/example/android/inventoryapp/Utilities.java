package com.example.android.inventoryapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


/**
 * Created by Thanassis on 7/5/2018.
 */

public class Utilities {

    private static String TAG = Utilities.class.getSimpleName();
    private static final String ORDER_BY_KEY = "select_order_by_key";

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    // convert from uri to bitmap
    public static Bitmap getBitmap(Context context, Uri imageUri){
        Bitmap bitmap = null;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Failed converting to Bitmap.", e);
        }

        return bitmap;
    }

    public static void setOrderByPreference(Context context, String orderValue){
        SharedPreferences preferences;
        preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        preferences.edit().putString(ORDER_BY_KEY, orderValue).apply();
    }
    public static String retrieveOrderByPreference(Context context){
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return preferences.getString(ORDER_BY_KEY, null);
    }
}
