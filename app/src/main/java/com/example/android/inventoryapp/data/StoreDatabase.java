package com.example.android.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

/**
 * Created by Thanassis on 26/4/2018.
 * Singleton Pattern DB.
 */

public class StoreDatabase {

    private static StoreDatabase Instance;
    private SQLiteDatabase sqLiteDatabase;

    /**
     * Create a database to read/write from it.
     *
     * @param context of the app
     */
    private StoreDatabase(Context context) {
        this.sqLiteDatabase = new StoreDbHelper(context).getWritableDatabase();
    }

    public static StoreDatabase getInstance(Context context) {
        if (Instance == null) {
            Instance = new StoreDatabase(context);
        }
        return Instance;
    }

    public Cursor query(String tableName, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public Cursor query(String table, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        Cursor cursor = sqLiteDatabase.query(
                table,
                projection,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy);

        return cursor;
    }

    public long insert(String tableName, ContentValues values) {
        return insert(tableName, null, values);
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {
        // Insert the new row, returning the primary key value of the new row
        return sqLiteDatabase.insert(table, nullColumnHack, values);
    }

    public int delete(String tableName,
                      String whereClause, String[] whereArgs){
        return sqLiteDatabase.delete(tableName, whereClause, whereArgs);
    }


    public int update(String table, ContentValues contentValues){
        return update(table, contentValues, null, null);
    }
    public int update(String tableName, ContentValues contentValues, String whereClause, String[] whereArgs){
        return sqLiteDatabase.update(tableName, contentValues, whereClause, whereArgs);
    }

    /**
     * Insert into DB a new {@link Shirt}
     */
    public long insertData(Shirt shirt) {

        ContentValues values = new ContentValues();
        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME, shirt.getName());
        values.put(StoreContract.StoreEntry.COLUMN_IMAGES, shirt.getImages());
        values.put(StoreContract.StoreEntry.COLUMN_PRICE, shirt.getPrice());
        values.put(StoreContract.StoreEntry.COLUMN_SIZE, shirt.getSize());
        values.put(StoreContract.StoreEntry.COLUMN_QUANTITY, shirt.getQuantity());
        values.put(StoreContract.StoreEntry.COLUMN_SUPPLIER_NAME, shirt.getSupplierName());
        values.put(StoreContract.StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER, shirt.getSupplierPhoneNumber());
        // Insert the new row, returning the primary key value of the new row
        return sqLiteDatabase.insert(StoreContract.StoreEntry.TABLE_NAME, null, values);
    }

    public Cursor getAllData() {
        // Define a projection that specifies which columns from the database will be shown after this query.
        String[] projection = {
                StoreContract.StoreEntry._ID,
                StoreContract.StoreEntry.COLUMN_PRODUCT_NAME,
                StoreContract.StoreEntry.COLUMN_IMAGES,
                StoreContract.StoreEntry.COLUMN_PRICE,
                StoreContract.StoreEntry.COLUMN_SIZE,
                StoreContract.StoreEntry.COLUMN_QUANTITY,
                StoreContract.StoreEntry.COLUMN_SUPPLIER_NAME,
                StoreContract.StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        Cursor cursor = sqLiteDatabase.query(
                StoreContract.StoreEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        
        return cursor;
    }
}
