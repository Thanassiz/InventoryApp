package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.inventoryapp.data.StoreContract.*;

/**
 * Created by Thanassis on 26/4/2018.
 */

public class StoreDbHelper extends SQLiteOpenHelper {

    public static final String TAG = StoreDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "store.db";
    private static final int DATABASE_VERSION = 1;

    public StoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the shirts table
        String SQL_CREATE_SHIRTS_TABLE = "CREATE TABLE " + StoreEntry.TABLE_NAME + " (" +
                StoreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StoreEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                StoreEntry.COLUMN_IMAGES + " BLOB, " +
                StoreEntry.COLUMN_PRICE + " REAL NOT NULL, " +
                StoreEntry.COLUMN_SIZE + " INTEGER NOT NULL, " +
                StoreEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                StoreEntry.COLUMN_SUPPLIER_NAME + " TEXT, " +
                StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " TEXT);";

                // Execute the SQL statement
                db.execSQL(SQL_CREATE_SHIRTS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
