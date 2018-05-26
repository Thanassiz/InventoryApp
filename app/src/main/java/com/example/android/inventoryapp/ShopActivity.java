package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.StoreContract;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String TAG = ShopActivity.class.getSimpleName();
    private final int LOADER_ID = 0;
    @BindView(R.id.shop_recycler_view)
    RecyclerView recyclerBasketView;
    @BindView(R.id.buy_button)
    Button buyButton;
    private ArrayList<String> selectedItems = new ArrayList<>();
    CheckOutShirtAdapter adapter;
    float finalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        ButterKnife.bind(this);
        setTitle("Basket");

        if (getIntent() != null) {
            selectedItems = getIntent().getStringArrayListExtra("selected_list");
            Log.d(TAG, "selected List= " + selectedItems);
        }
        adapter = new CheckOutShirtAdapter(this, null, selectedItems);
        recyclerBasketView.setLayoutManager(new LinearLayoutManager(this));
        // Set divider in the list
        recyclerBasketView.addItemDecoration(new DividerItemDecoration(this, 0));
        recyclerBasketView.setAdapter(adapter);

        // Start the loader
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @OnClick(R.id.buy_button)
    void onClickBuy() {
        //region Quantity validation message ( if user hasn't added any quantity from selected item(s) )
        int totalSelectedItems = 0;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            String itemQuantity = ((TextView) recyclerBasketView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.shop_quantity_value)).getText().toString();
            totalSelectedItems = totalSelectedItems + Integer.parseInt(itemQuantity);

        }
        //endregion
        if (totalSelectedItems == 0) {
            Toast toast = Toast.makeText(this, R.string.toast_add_at_least_one_item, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Log.i(TAG, "Zero item(s) selected.");
        } else {
            // Update the selected item(s) quantity and show a Alert dialog (Total price) before buying.
            showBuyConfirmationDialog();
        }

    }

    private void showBuyConfirmationDialog() {
        //region Show total price
        for (int i = 0; i < adapter.getItemCount(); i++) {
            String itemQuantity = ((TextView) recyclerBasketView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.shop_quantity_value)).getText().toString();
            String itemTotalPrice = ((TextView) recyclerBasketView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.shop_price_value)).getText().toString();
            finalPrice = finalPrice + Float.parseFloat(itemTotalPrice);
        }
        //endregion

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Buy the selected item(s) ?\nTotal price: " + finalPrice + " $");
        // Reset total price after showing the message
        finalPrice = 0;
        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Proceed" button, so buy/update the item.
                buyItem();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue buying item(s).
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void buyItem() {
        ArrayList<ShirtViewModel> list = new ArrayList<>(CheckOutShirtAdapter.basketMap.values());
        ContentValues values = new ContentValues();
        int countUpdated = 0;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            for (ShirtViewModel shirt : list) {
                long id = shirt.getId();
                String shirtName = ((TextView) recyclerBasketView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.shop_name_label)).getText().toString();

                // Matching the items from HashMap with adapter's items by name
                if (shirt.getName().equals(shirtName)) {

                    // Update only the items that have higher quantity value from 0
                    String itemQuantity = ((TextView) recyclerBasketView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.shop_quantity_value)).getText().toString();
                    if (Integer.parseInt(itemQuantity) > 0) {
                        String where = StoreContract.StoreEntry._ID + "=?";
                        String[] selectionArgs = new String[]{"" + id};

                        values.put(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME, shirt.getName());
                        values.put(StoreContract.StoreEntry.COLUMN_PRICE, shirt.getPrice());
                        values.put(StoreContract.StoreEntry.COLUMN_SIZE, shirt.getSize());
                        // ONLY NEED TO UPDATE THIS, but other values are necessary for validation
                        int newQuantity = shirt.getQuantity() - Integer.parseInt(itemQuantity);
                        values.put(StoreContract.StoreEntry.COLUMN_QUANTITY, newQuantity);
                        values.put(StoreContract.StoreEntry.COLUMN_SUPPLIER_NAME, shirt.getSupplierName());
                        values.put(StoreContract.StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER, shirt.getSupplierPhoneNumber());
                        //Log.e(TAG, shirt.getName() + shirt.getPrice() + shirt.getSupplierName() + Integer.parseInt(itemQuantity));

                        int rowsUpdated = getContentResolver().update(StoreContract.StoreEntry.CONTENT_URI, values, where, selectionArgs);
                        countUpdated++;
                    }
                }
            }
        }
        // Display error message in Log if product stock fails to update
        if (countUpdated <= 0) {
            Toast.makeText(this, getString(R.string.error_update_item), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, countUpdated + " " + getString(R.string.toast_rows_updated), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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

        String selection = StoreContract.StoreEntry._ID + "=?";
        String[] selectionArgs = new String[selectedItems.size()];
        selectionArgs = selectedItems.toArray(selectionArgs);
        StringBuilder builder = new StringBuilder();
        builder.append(selection);
        for (int j = 0; j < selectedItems.size(); j++) {
            if (j > 0) {
                builder.append(" OR " + StoreContract.StoreEntry._ID + "=?");
            }
            Log.d(TAG, builder.toString());
            Log.d(TAG, "array item on position: " + j + " with id= " + selectionArgs[j]);
        }
        selection = builder.toString();
        return new CursorLoader(this,
                StoreContract.StoreEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        CheckOutShirtAdapter.basketMap.clear();
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        CheckOutShirtAdapter.basketMap.clear();
        adapter.swapCursor(null);
    }
}
