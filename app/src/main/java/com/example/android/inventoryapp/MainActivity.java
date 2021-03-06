package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.inventoryapp.data.StoreContract;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = MainActivity.class.getSimpleName();
    private final int LOADER_ID = 0;
    @BindView(R.id.order_by_spinner)
    AppCompatSpinner spinner;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.fab_add)
    FloatingActionButton addFAB;
    @BindView(R.id.fab_delete)
    FloatingActionButton deleteFAB;
    @BindView(R.id.fab_shop)
    FloatingActionButton shopFAB;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty_view)
    RelativeLayout emptyView;
    private ShirtAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //String json = "http://api.shopstyle.com/api/v2/products?pid=uid6276-40892826-2&fts=t+shirt&offset=0&limit=10";
        // add mock data
      /*  for (int i = 1; i < 21 ; i++) {
            byte[] expectedRead = new byte[] { (byte) 129, (byte) 130, (byte) 131};
            Shirt shirt = new Shirt( "Levi's T-shirt: " + i, expectedRead, (float) 14.655, 2, 1, "Levi' Store", "1234567890" );
            StoreDatabase.getInstance( this ).insertData( shirt );
        }
*/
        setSpinner();
        searchProduct();
        cursorAdapter = new ShirtAdapter(this, null);
        // Sets layout manager for recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Set divider in the list
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), 0));
        recyclerView.setAdapter(cursorAdapter);
        // Start the loader
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_store:
                return true;
            case R.id.menu_delete_all:
                deleteAllData();
                return true;
            case R.id.menu_online_store:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Go to product activity, no uri provided.
     */
    @OnClick(R.id.fab_add)
    void onClick() {
        Intent intent = new Intent(MainActivity.this, ProductActivity.class);
        startActivity(intent);
    }

    /**
     * Delete items from DB which they are selected from checkboxes. (specified by their ID)
     */
    @OnClick(R.id.fab_delete)
    void onClickDelete() {
        // Find if there is an item to delete or a selected item and show the appropriate message.
        ArrayList<ShirtViewModel> list = new ArrayList<>(ShirtAdapter.shirtMap.values());
        boolean isSelected = false;
        for (ShirtViewModel item : list) {
            if (item.isChecked) {
                isSelected = true;
            }
        }

        if (ShirtAdapter.shirtMap.values().size() == 0) {
            Log.i(TAG, "There is no item to delete.");
        } else if (!isSelected) {
            Toast toast = Toast.makeText(this, R.string.toast_no_items_selected, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            // Delete the selected item(s) and show a Alert dialog before deletion.
            showDeleteConfirmationDialog();
        }
    }

    /**
     * Go to product activity, passing the selected ids
     */
    @OnClick(R.id.fab_shop)
    void onClickShop() {
        ArrayList<ShirtViewModel> list = new ArrayList<>(ShirtAdapter.shirtMap.values());
        ArrayList<String> selectedItems = new ArrayList<>();
        boolean isSelected = false;
        for (ShirtViewModel item : list) {
            if (item.isChecked) {
                long id = item.getId();
                isSelected = true;
                selectedItems.add(String.valueOf(id));
                Log.i(TAG, "Shirt added with id: " + item.getId());
            }
        }
        if (ShirtAdapter.shirtMap.values().size() == 0) {
            Log.i(TAG, "There is no item to buy.");
        } else if (!isSelected) {
            Toast toast = Toast.makeText(this, R.string.toast_no_items_selected, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            Intent intent = new Intent(MainActivity.this, ShopActivity.class);
            intent.putStringArrayListExtra("selected_list", selectedItems);
            startActivity(intent);
        }
    }

    private void deleteSelectedItem() {
        ArrayList<ShirtViewModel> list = new ArrayList<>(ShirtAdapter.shirtMap.values());
        String where = StoreContract.StoreEntry._ID + "=?";
        ArrayList<String> selectedIds = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        builder.append(where);
        int countCheck = 0;
        for (ShirtViewModel item : list) {
            long id = item.getId();
            if (item.isChecked) {
                if (countCheck > 0) {
                    builder.append(" OR " + StoreContract.StoreEntry._ID + "=?");
                }
                countCheck++;
                selectedIds.add(String.valueOf(id));
            }
        }
        where = builder.toString();
        Log.d(TAG, where);
        String[] selectionArgs = new String[selectedIds.size()];
        selectionArgs = selectedIds.toArray(selectionArgs);
        int rowsDeleted = getContentResolver().delete(StoreContract.StoreEntry.CONTENT_URI, where, selectionArgs);
        if (rowsDeleted > 0) {
            Toast.makeText(this, rowsDeleted + " " + getString(R.string.toast_rows_deleted), Toast.LENGTH_SHORT).show();
            Log.i(TAG, rowsDeleted + " " + getString(R.string.toast_rows_deleted));
        } else {
            Log.i(TAG, getString(R.string.error_delete_item));
        }
    }

    private void deleteAllData() {
        int rowsDeleted = getContentResolver().delete(StoreContract.StoreEntry.CONTENT_URI, null, null);
        if (rowsDeleted > 0) {
            Toast.makeText(this, rowsDeleted + " " + getString(R.string.toast_rows_deleted), Toast.LENGTH_SHORT).show();
            Log.e(TAG, rowsDeleted + " " + getString(R.string.toast_rows_deleted));
        } else {
            Log.e(TAG, getString(R.string.error_delete_item));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String querySortOder = null;
        String selection = null;
        String[] selectionArgs = null;
        final String SORT_ORDER_ASC = " ASC";
        final String SORT_ORDER_DESC = " DESC";
        String orderBy = Utilities.retrieveOrderByPreference(this);
        if (orderBy != null) {
            if (orderBy.contentEquals(getString(R.string.order_by_best_match))) {
                querySortOder = StoreContract.StoreEntry._ID + SORT_ORDER_DESC; //Show the newest added shirts first
            } else if (orderBy.contentEquals(getString(R.string.order_by_lowest))) {
                querySortOder = StoreContract.StoreEntry.COLUMN_PRICE + SORT_ORDER_ASC; // Lowest price first
            } else if (orderBy.contentEquals(getString(R.string.order_by_highest))) {
                querySortOder = StoreContract.StoreEntry.COLUMN_PRICE + SORT_ORDER_DESC; // Highest price first
            } else if (orderBy.contentEquals(getString(R.string.order_by_available))) {
                selection = StoreContract.StoreEntry.COLUMN_QUANTITY + "!=?";
                selectionArgs = new String[]{"0"};
                querySortOder = StoreContract.StoreEntry.COLUMN_QUANTITY + SORT_ORDER_ASC; // Lowest quantity availability first (removes not available)
            }
        }
        if (bundle != null) {
            selection = bundle.getString("select");
            selectionArgs = bundle.getStringArray("select_args");
        }
        // Define a projection that specifies which columns from the database will be shown after this query.
        String[] projection = {StoreContract.StoreEntry._ID, StoreContract.StoreEntry.COLUMN_PRODUCT_NAME, StoreContract.StoreEntry.COLUMN_IMAGES, StoreContract.StoreEntry.COLUMN_PRICE, StoreContract.StoreEntry.COLUMN_SIZE, StoreContract.StoreEntry.COLUMN_QUANTITY, StoreContract.StoreEntry.COLUMN_SUPPLIER_NAME, StoreContract.StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER};
        return new CursorLoader(this, StoreContract.StoreEntry.CONTENT_URI, projection, selection, selectionArgs, querySortOder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ShirtAdapter.shirtMap.clear();
        cursorAdapter.changeCursor(cursor);
        if (cursor.getCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ShirtAdapter.shirtMap.clear();
        cursorAdapter.swapCursor(null);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete the selected item(s) ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteSelectedItem();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void setSpinner() {
        // Spinner List names
        final ArrayList<String> spinnerList = new ArrayList<String>() {{
            add(getString(R.string.order_by_best_match));
            add(getString(R.string.order_by_lowest));
            add(getString(R.string.order_by_highest));
            add(getString(R.string.order_by_available));
        }};
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList));
        String preference = Utilities.retrieveOrderByPreference(this);
        if (!Utilities.isEmptyString(preference)) {
            for (int i = 0;
                 i < spinnerList.size();
                 i++) {
                if (preference.contentEquals(spinnerList.get(i))) {
                    spinner.setSelection(i);
                }
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(TAG, "SPINNER ->" + position);
                switch (position) {
                    case 0:
                        Utilities.setOrderByPreference(MainActivity.this, spinnerList.get(position));
                        getLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
                        cursorAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        Utilities.setOrderByPreference(MainActivity.this, spinnerList.get(position));
                        getLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
                        cursorAdapter.notifyDataSetChanged();
                        break;
                    case 2:
                        Utilities.setOrderByPreference(MainActivity.this, spinnerList.get(position));
                        getLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
                        cursorAdapter.notifyDataSetChanged();
                        break;
                    case 3:
                        Utilities.setOrderByPreference(MainActivity.this, spinnerList.get(position));
                        getLoaderManager().restartLoader(LOADER_ID, null, MainActivity.this);
                        cursorAdapter.notifyDataSetChanged();
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void searchProduct() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, query);
                Bundle bundle = new Bundle();
                String selection = StoreContract.StoreEntry.COLUMN_PRODUCT_NAME + " LIKE ?";
                String[] selectionArgs = new String[]{"%" + query + "%"};
                bundle.putString("select", selection);
                bundle.putStringArray("select_args", selectionArgs);
                getLoaderManager().restartLoader(LOADER_ID, bundle, MainActivity.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /*   private void displayDB(){

        TextView tempView = findViewById(R.id.temporary_textView);
        tempView.setText("The shirts table contains \n\n");
        tempView.append(StoreContract.StoreEntry._ID + " - " +
                StoreContract.StoreEntry.COLUMN_PRODUCT_NAME + " - " +
                StoreContract.StoreEntry.COLUMN_IMAGES + " - " +
                StoreContract.StoreEntry.COLUMN_PRICE + " - " +
                StoreContract.StoreEntry.COLUMN_SIZE + " - " +
                StoreContract.StoreEntry.COLUMN_QUANTITY + " - " +
                StoreContract.StoreEntry.COLUMN_SUPPLIER_NAME + " - " +
                StoreContract.StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n\n");

       // add mock data to database
        for (int i = 0; i < 15 ; i++) {
            byte[] expectedRead = new byte[] { (byte) 129, (byte) 130, (byte) 131};
            Shirt shirt = new Shirt("Levi's T-shirt version: " + i, expectedRead, (float) 14.655, 2, 1, "Levi' Store", "1234567890" );
            StoreDatabase.getInstance(this).insertData(shirt);
        }
        // get from DB
        List<Shirt> shirts = new ArrayList<>();
        shirts = StoreDatabase.getInstance(this).getAllData();

        for (int i = 0; i < shirts.size() ; i++) {

            tempView.append("\n" + " _________________________________________________________ " +
                    shirts.get(i).getId()+ " - " +
                    shirts.get(i).getName() + " - " +
                    shirts.get(i).getImages() + " - " +
                    shirts.get(i).getPrice() + " - " +
                    shirts.get(i).getSize() + " - " +
                    shirts.get(i).getQuantity() + " - " +
                    shirts.get(i).getSupplierName() + " - " +
                    shirts.get(i).getSupplierPhoneNumber() + "\n");
        }
    }
    */
}
