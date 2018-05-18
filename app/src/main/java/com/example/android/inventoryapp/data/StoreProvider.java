package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.inventoryapp.R;

/**
 * Created by Thanassis on 6/5/2018.
 */

public class StoreProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    private static final String TAG = StoreProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the Shirts table
     */
    private static final int SHIRTS = 100;

    /**
     * URI matcher code for the content URI for a single item in the Shirts table
     */
    private static final int SHIRT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /** Static initializer. This is run the first time anything is called from this class. */
    static {
        // The content URI of the form "content://com.example.android.inventoryapp/shirts" will map to the
        // integer code {@link #SHIRTS}. This URI is used to provide access to MULTIPLE rows of the table.
        uriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_SHIRTS, SHIRTS);
        // The content URI of the form "content://com.example.android.inventoryapp/shirts/#" will map to the
        // integer code {@link #SHIRT_ID}. This URI is used to provide access to ONE single row of the table.
        uriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_SHIRTS + "/#", SHIRT_ID);
    }

    @Override
    public boolean onCreate() {
        StoreDatabase.getInstance(getContext());
        return true;
    }

    /**
     * Function - READ from table
     * Peform query for given URI and load the cursor with results fetched from table.
     * The returned result can have multiple rows or a single row, depending on given URI.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = uriMatcher.match(uri);
        switch (match) {
            case SHIRTS:
                cursor = StoreDatabase.getInstance(getContext()).query(
                        StoreContract.StoreEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);
                break;
            case SHIRT_ID:
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = StoreDatabase.getInstance(getContext()).query(StoreContract.StoreEntry.TABLE_NAME,
                        projection, selection, selectionArgs, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.exception_unknown_uri) + uri);
        }
        // Set notification URI on Cursor so it knows when to update in the event the data in cursor changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Function - INSERT into table
     * This method inserts records in the table
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case SHIRTS:
                return insertShirt(uri, contentValues);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.exception_insert_unsupported_uri) + uri);
        }
    }

    private Uri insertShirt(Uri uri, ContentValues values) {
        long id;
        boolean isValid = validationData(values);
        if (isValid) {
            id = StoreDatabase.getInstance(getContext()).insert(StoreContract.StoreEntry.TABLE_NAME, values);
        } else {
            id = -1;
        }
        if (id == -1) {
            Log.e(TAG, getContext().getString(R.string.error_insert_data) + uri);
            return null;
        }

        // Notify all listeners that the data has changed for this content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int rowsDeleted;
        int match = uriMatcher.match(uri);
        switch (match) {
            case SHIRTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = StoreDatabase.getInstance(getContext()).delete(StoreContract.StoreEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case SHIRT_ID:
                // Delete a single row given by the ID in the URI
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = StoreDatabase.getInstance(getContext()).delete(StoreContract.StoreEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.error_delete_data) + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String whereClause, @Nullable String[] whereArgs) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case SHIRTS:
                return updateProduct(uri, contentValues, whereClause, whereArgs);
            case SHIRT_ID:
                whereClause = StoreContract.StoreEntry._ID + "=?";
                whereArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, whereClause, whereArgs);
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.error_update_data) + uri);

        }
    }

    private int updateProduct(Uri uri, ContentValues contentValues, String whereClause, String[] whereArgs) {

        int rowsUpdated = 0;
        boolean isValid = validationData(contentValues);
        if (isValid) {
            rowsUpdated = StoreDatabase.getInstance(getContext()).update(StoreContract.StoreEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
            // If 1 or more rows were updated, then notify that the data at the given uri has changed.
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        if (rowsUpdated == 0){
            Log.e(TAG, "Nothing updated");
        }
        return rowsUpdated;
    }

    /**
     * Method to determine type of URI used to query the table
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case SHIRTS:
                return StoreContract.StoreEntry.CONTENT_LIST_TYPE;
            case SHIRT_ID:
                return StoreContract.StoreEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.exception_unknown_uri) + uri);
        }
    }

    private boolean validationData(ContentValues values) {
        //TODO validation Images
        // Check that the name is not null
        String productName = values.getAsString(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME);
        if (productName == null || productName.trim().length() == 0 || productName.isEmpty()) {
            throw new IllegalArgumentException(getContext().getString(R.string.exception_invalid_product_name));
        }
        // Check that the size is valid
        Integer size = values.getAsInteger(StoreContract.StoreEntry.COLUMN_SIZE);
        if (size == null || !StoreContract.StoreEntry.isValidSize(size)) {
            throw new IllegalArgumentException(getContext().getString(R.string.exception_invalid_product_size));
        }
        // Check if Price is valid
        float productPrice = values.getAsFloat(StoreContract.StoreEntry.COLUMN_PRICE);
        if (productPrice < 0.0) {
            throw new IllegalArgumentException(getContext().getString(R.string.exception_invalid_price));
        }
        // Check if Quantity is valid
        int productQuantity = values.getAsInteger(StoreContract.StoreEntry.COLUMN_QUANTITY);
        if (productQuantity < 0) {
            throw new IllegalArgumentException(getContext().getString(R.string.exception_invalid_quantity));
        }
        // Check if Supplier name  is valid
        String productSupplierName = values.getAsString(StoreContract.StoreEntry.COLUMN_SUPPLIER_NAME);
        if (productSupplierName == null || productSupplierName.isEmpty() || productSupplierName.trim().length() == 0) {
            throw new IllegalArgumentException(getContext().getString(R.string.exception_invalid_supplier_name));
        }
        // Check if Supplier phone  is valid
        String productSupplierPhone = values.getAsString(StoreContract.StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        if (productSupplierPhone == null || productSupplierPhone.isEmpty() || productSupplierPhone.trim().length() == 0 || productSupplierPhone.trim().length() > 10) {
            throw new IllegalArgumentException(getContext().getString(R.string.exception_invalid_supplier_phone));
        }
        return true;
    }
}
