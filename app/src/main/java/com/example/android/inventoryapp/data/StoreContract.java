package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Thanassis on 26/4/2018.
 */

public final class StoreContract {

    private StoreContract(){}

    /** ContentProvider Authority Name */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    /** ContentProvider Base Uri */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /** Possible path (appended to base content URI for possible URI's) */
    public static final String PATH_SHIRTS = "shirts";

    public static final class StoreEntry implements BaseColumns {

        /** The content URI to access the data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SHIRTS);
        /** MIME type of the {@link #CONTENT_URI} for a list of items */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHIRTS;
        /** MIME type of the {@link #CONTENT_URI} for a single item */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHIRTS;

        public static final String TABLE_NAME = "shirts";
        public static  final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_IMAGES = "images";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

        /**
         * Possible values for the size of the shirt.
         */
        public static final int SIZE_XS = 0;
        public static final int SIZE_S = 1;
        public static final int SIZE_M = 2;
        public static final int SIZE_L = 3;
        public static final int SIZE_XL = 4;

        /**
         * Returns whether or not the given shirt size is one of choices.
         */
        public static boolean isValidSize(int size) {
            if (size == SIZE_XS || size == SIZE_S || size == SIZE_M ||
                    size == SIZE_L || size == SIZE_XL) {
                return true;
            }
            return false;
        }
    }

}
