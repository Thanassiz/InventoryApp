package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.inventoryapp.data.StoreContract;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String TAG = ShopActivity.class.getSimpleName();
    @BindView(R.id.shop_recycler_view)
    RecyclerView recyclerBasketView;
    @BindView(R.id.total_price_value)
    TextView totalPrice;
    @BindView(R.id.buy_button)
    Button buyButton;
    private ArrayList<String> selectedItems = new ArrayList<>();
    CheckOutShirtAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        ButterKnife.bind(this);
        setTitle("Basket");

        if (getIntent() != null) {
            selectedItems = getIntent().getStringArrayListExtra("selected_list");
            Log.e(TAG, "selected List= " + selectedItems);
        }
        adapter = new CheckOutShirtAdapter(this, null, selectedItems);
        recyclerBasketView.setLayoutManager(new LinearLayoutManager(this));
        // Set divider in the list
        recyclerBasketView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerBasketView.setAdapter(adapter);

        // Start the loader
        getLoaderManager().initLoader(0, null, this);
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
        //String[] selectionArgs = new String[]{""};
        String[] selectionArgs = new String[selectedItems.size()];
        selectionArgs = selectedItems.toArray(selectionArgs);
        StringBuilder builder = new StringBuilder();
        builder.append(selection);
        for (int j = 0; j <selectedItems.size() ; j++) {
            if (j > 0){
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
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
