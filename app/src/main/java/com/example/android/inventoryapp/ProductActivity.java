package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.StoreContract;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = ProductActivity.class.getSimpleName();
    private static final int IMAGE_REQUEST_CODE = 0;
    private final int ITEM_LOADER = 0;
    // Received uri
    Uri receivedUri = null;

    @BindView(R.id.new_product_name_value)
    EditText nameEditText;
    @BindView(R.id.new_product_price_value)
    EditText priceEditText;
    @BindView(R.id.new_product_size_value)
    EditText sizeEditText;
    @BindView(R.id.new_product_quantity_value)
    EditText quantityEditText;
    @BindView(R.id.new_product_supplier_name_value)
    EditText suppNameEditText;
    @BindView(R.id.new_product_supplier_phone_value)
    EditText suppPhoneEditText;
    @BindView(R.id.new_product_add_image_button)
    Button addImageButton;
    @BindView(R.id.new_product_thumbnail)
    ImageView thumbnailView;
    private String name;
    private String price;
    private String size;
    private byte[] images;
    private String quantity;
    private String suppName;
    private String suppPhone;
    // Flag that keeps track of whether the item has been edited
    private boolean itemHasChanged = false;
    // Listener for any user touches on a View, implying that they are modifying view
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            itemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        receivedUri = intent.getData();
        if (receivedUri == null) {
            setTitle(getString(R.string.product_new_activity_label));
        } else {
            setTitle(getString(R.string.product_edit_activity_label));
            getLoaderManager().initLoader(ITEM_LOADER, null, this);
        }

        nameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        sizeEditText.setOnTouchListener(touchListener);
        quantityEditText.setOnTouchListener(touchListener);
        suppNameEditText.setOnTouchListener(touchListener);
        suppPhoneEditText.setOnTouchListener(touchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.product_menu_save:
                if (receivedUri == null) {
                    addProduct();
                } else {
                    updateProduct();
                }
                return true;
            // Case of Back Button in toolbar
            case android.R.id.home:
                // If the item hasn't changed, go back
                if (!itemHasChanged) {
                    NavUtils.navigateUpFromSameTask(ProductActivity.this);
                    return true;
                }
                // Else, setup dialog to warn user
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(ProductActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addProduct() {
        if (isEmptyValidation()) {
            if (isDuplicatedItem(name, price, size, suppName, suppPhone)) {
                updateProduct(quantity);
            } else {
                ContentValues values = new ContentValues();
                values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME, name);
                values.put(StoreContract.StoreEntry.COLUMN_IMAGES, images);
                values.put(StoreContract.StoreEntry.COLUMN_PRICE, price);
                values.put(StoreContract.StoreEntry.COLUMN_SIZE, size);
                values.put(StoreContract.StoreEntry.COLUMN_QUANTITY, quantity);
                values.put(StoreContract.StoreEntry.COLUMN_SUPPLIER_NAME, suppName);
                values.put(StoreContract.StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER, suppPhone);

                Uri currentUri = getContentResolver().insert(StoreContract.StoreEntry.CONTENT_URI, values);
                if (currentUri == null) {
                    Toast.makeText(this, "Failed to insert data.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Successful insert.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

        }
    }

    private void updateProduct() {
        if (isEmptyValidation()) {
            ContentValues values = new ContentValues();
            values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME, name);
            values.put(StoreContract.StoreEntry.COLUMN_IMAGES, images);
            values.put(StoreContract.StoreEntry.COLUMN_PRICE, price);
            values.put(StoreContract.StoreEntry.COLUMN_SIZE, size);
            values.put(StoreContract.StoreEntry.COLUMN_QUANTITY, quantity);
            values.put(StoreContract.StoreEntry.COLUMN_SUPPLIER_NAME, suppName);
            values.put(StoreContract.StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER, suppPhone);

            int rowsUpdated = getContentResolver().update(receivedUri, values, null, null);

            // Display error message in Log if product stock fails to update
            if (rowsUpdated <= 0) {
                Toast.makeText(this, getString(R.string.error_update_item), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, rowsUpdated + " " + getString(R.string.toast_rows_updated), Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    /** Update without Validation cause validation has already been made on Add product method (when calling this)
     * skipping validation so initialization of quantity wont happen */
    private void updateProduct(String quantity) {
            ContentValues values = new ContentValues();
            values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME, name);
            values.put(StoreContract.StoreEntry.COLUMN_IMAGES, images);
            values.put(StoreContract.StoreEntry.COLUMN_PRICE, price);
            values.put(StoreContract.StoreEntry.COLUMN_SIZE, size);
            values.put(StoreContract.StoreEntry.COLUMN_QUANTITY, quantity);
            values.put(StoreContract.StoreEntry.COLUMN_SUPPLIER_NAME, suppName);
            values.put(StoreContract.StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER, suppPhone);

            int rowsUpdated = getContentResolver().update(receivedUri, values, null, null);

            // Display error message in Log if product stock fails to update
            if (rowsUpdated <= 0) {
                Toast.makeText(this, getString(R.string.error_update_item), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, rowsUpdated + " " + getString(R.string.toast_rows_updated), Toast.LENGTH_SHORT).show();
                finish();
            }
    }

    private boolean isEmptyValidation() {
        name = nameEditText.getText().toString().trim();
        price = priceEditText.getText().toString().trim();
        size = sizeEditText.getText().toString().trim();
        quantity = quantityEditText.getText().toString().trim();
        suppName = suppNameEditText.getText().toString().trim();
        suppPhone = suppPhoneEditText.getText().toString().trim();

        // Check product name
        if (TextUtils.isEmpty(name) || name.length() == 0) {
            nameEditText.requestFocus();
            nameEditText.setError(getString(R.string.error_empty_field));
            return false;
        }
        // Check price
        if (TextUtils.isEmpty(price) || price.length() == 0) {
            priceEditText.requestFocus();
            priceEditText.setError(getString(R.string.error_empty_field));
            return false;
        } else {
            float floatPrice = Float.valueOf(price);
            if (floatPrice < 0.0) {
                priceEditText.setError(getString(R.string.error_insert_valid_data));
                return false;
            }
            // Check size
            if (TextUtils.isEmpty(size) || size.length() == 0) {

                sizeEditText.requestFocus();
                sizeEditText.setError(getString(R.string.error_empty_field));
                return false;
            } else {
                int intSize = Integer.parseInt(size);
                if (intSize < 0 || intSize > 4) {
                    sizeEditText.setError(getString(R.string.error_invalid_size_input));
                    return false;
                }
            }
            // Check quantity
            if (TextUtils.isEmpty(quantity) || quantity.length() == 0) {
                quantityEditText.requestFocus();
                quantityEditText.setError(getString(R.string.error_empty_field));
                return false;
            } else {
                int intQuantity = Integer.parseInt(quantity);
                if (intQuantity < 0) {
                    quantityEditText.setError(getString(R.string.error_insert_valid_data));
                    return false;
                }
                // Check Supplier Name
                if (TextUtils.isEmpty(suppName) || suppName.length() == 0) {
                    suppNameEditText.requestFocus();
                    suppNameEditText.setError(getString(R.string.error_empty_field));
                    return false;
                }
                // Check Supplier Phone
                if (TextUtils.isEmpty(suppPhone) || suppPhone.length() == 0 || suppPhone.length() > 10) {
                    suppPhoneEditText.requestFocus();
                    suppPhoneEditText.setError(getString(R.string.error_empty_field));
                    return false;
                }
                // Check product image
                if (images == null) {
                    Toast.makeText(this, "Image required", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }
        }
    }

    // Check if Name, Size, Price, SupplierName and Phone are same with an item from DB, if yes update quantity else insert new.
    private boolean isDuplicatedItem(String name, String price, String size, String suppName, String suppPhone) {
        ArrayList<ShirtViewModel> list = new ArrayList<>(ShirtAdapter.shirtMap.values());
        for (ShirtViewModel shirt : list) {
            if (name.equals(shirt.getName()) &&
                    String.valueOf(Float.parseFloat(price)).equals(String.valueOf(shirt.getPrice())) &&
                    size.equals(String.valueOf(shirt.getSize())) &&
                    suppName.equals(shirt.getSupplierName()) &&
                    suppPhone.equals(shirt.getSupplierPhoneNumber())
                    ) {
                int newQuantity = shirt.getQuantity() + Integer.parseInt(quantity);
                long id = shirt.getId();
                quantity = String.valueOf(newQuantity);
                receivedUri = ContentUris.withAppendedId(StoreContract.StoreEntry.CONTENT_URI, id);
                return true;
            }
        }
        return false;
    }

    @OnClick(R.id.new_product_add_image_button)
    void onClick() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select thumbnail"), IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = Utilities.getBitmap(this, selectedImage);
            images = Utilities.getBytes(bitmap);
            thumbnailView.setImageBitmap(bitmap);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                StoreContract.StoreEntry._ID,
                StoreContract.StoreEntry.COLUMN_PRODUCT_NAME,
                StoreContract.StoreEntry.COLUMN_IMAGES,
                StoreContract.StoreEntry.COLUMN_PRICE,
                StoreContract.StoreEntry.COLUMN_SIZE,
                StoreContract.StoreEntry.COLUMN_QUANTITY,
                StoreContract.StoreEntry.COLUMN_SUPPLIER_NAME,
                StoreContract.StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(this,            // Parent activity context
                receivedUri,    // Query the content URI for the current item
                projection,                               // Columns to include in the resulting Cursor
                null,                            // No selection clause
                null,                         // No selection arguments
                null);                          // No sorting order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Figure out the index of each column
        int idColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME);
        int imageColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_IMAGES);
        int priceColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRICE);
        int sizeColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_SIZE);
        int quantityColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_QUANTITY);
        int supplyNameColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_SUPPLIER_NAME);
        int supplyPhoneNumberColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        // Proceed with moving to the first row of the cursor and reading data from it. (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            int currentId = cursor.getInt(idColumnIndex);
            name = cursor.getString(nameColumnIndex);
            images = cursor.getBlob(imageColumnIndex);
            price = cursor.getString(priceColumnIndex);
            size = cursor.getString(sizeColumnIndex);
            quantity = cursor.getString(quantityColumnIndex);
            suppName = cursor.getString(supplyNameColumnIndex);
            suppPhone = cursor.getString(supplyPhoneNumberColumnIndex);

            nameEditText.setText(name);
            priceEditText.setText(String.valueOf(price));
            sizeEditText.setText(String.valueOf(size));
            quantityEditText.setText(String.valueOf(quantity));
            suppNameEditText.setText(suppName);
            suppPhoneEditText.setText(suppPhone);
            thumbnailView.setImageBitmap(Utilities.getBitmap(images));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        sizeEditText.setText("");
        quantityEditText.setText("");
        suppNameEditText.setText("");
        suppPhoneEditText.setText("");
        thumbnailView.setImageResource(R.drawable.image_thumbnail);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit ?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                // User clicked the "Stay" button, so dismiss the dialog
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the item hasn't changed, go back
        if (!itemHasChanged) {
            super.onBackPressed();
            return;
        }
        // Else, setup a dialog to warn the user.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
}
