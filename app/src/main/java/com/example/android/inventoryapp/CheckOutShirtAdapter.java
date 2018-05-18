package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.inventoryapp.data.StoreContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Thanassis on 17/5/2018.
 */

public class CheckOutShirtAdapter extends RecyclerViewCursorAdapter<CheckOutShirtAdapter.BasketViewHolder> {
    private String TAG = CheckOutShirtAdapter.class.getSimpleName();
    private Context context;
    private Cursor cursor;
    private ArrayList<String> selectedItems = new ArrayList<>();


    public CheckOutShirtAdapter(Context context, Cursor cursor, ArrayList<String> selectedItems) {
        super(context, cursor);
        this.context = context;
        this.cursor = cursor;
        this.selectedItems = selectedItems;
    }


    @Override
    public BasketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shirt_shop_recycler_layout, parent, false);
        return new BasketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BasketViewHolder viewHolder, Cursor cursor, int position) {
        List<ShirtViewModel> shirts = fromCursor(cursor);
        viewHolder.bindBasketView(shirts, selectedItems);
    }

    public class BasketViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.shop_name_label)
        TextView nameItemView;
        @BindView(R.id.shop_size_label)
        TextView sizeItemView;
        @BindView(R.id.shop_quantity_label)
        TextView quantityItemView;
        @BindView(R.id.shop_minus)
        Button minusButton;
        @BindView(R.id.shop_plus)
        Button plusButton;
        @BindView(R.id.shop_quantity_value)
        TextView quantityBaskeView;
        @BindView(R.id.shop_price_value)
        TextView priceBasketView;
        private ShirtViewModel shirt;

        BasketViewHolder(View basketItemView) {
            super(basketItemView);
            ButterKnife.bind(this, basketItemView);
        }

        public void bindBasketView(List<ShirtViewModel> shirts, ArrayList<String> selectedItems) {
            long id = Long.parseLong(selectedItems.get(getAdapterPosition()));
            shirt = shirts.get(getAdapterPosition());
            if (id == shirt.getId()) {
                nameItemView.setText(shirt.getName());
                sizeItemView.setText(String.valueOf(shirt.getSize()));
                quantityItemView.setText(String.valueOf(shirt.getQuantity()));
            }
            quantityBaskeView.setText(String.valueOf(0));
            priceBasketView.setText(String.valueOf(0.00));

        }

        @OnClick(R.id.shop_plus)
        void onClickPlus() {
            if (shirt != null) {
                quantityPlusValidation(Integer.parseInt(quantityBaskeView.getText().toString()), Integer.parseInt(quantityItemView.getText().toString()), shirt);
            }
        }

        @OnClick(R.id.shop_minus)
        void onClickMinus() {
            if (shirt != null) {
                quantityMinusValidation(Integer.parseInt(quantityBaskeView.getText().toString()), shirt);
            }
        }


        public void quantityPlusValidation(int currentQuantity, int itemQuantity, ShirtViewModel shirt) {
            if (currentQuantity >= itemQuantity) {
                Log.i(TAG, "Not enough items on stock.");
            } else {
                currentQuantity++;
                quantityBaskeView.setText(String.valueOf(currentQuantity));
                float sumItemPrice = currentQuantity * shirt.getPrice();
                String formattedString = String.format("%.02f", sumItemPrice);
                priceBasketView.setText(formattedString);
            }
        }

        public void quantityMinusValidation(int currentQuantity, ShirtViewModel shirt) {
            if (currentQuantity <= 0) {
                Log.i(TAG, "Cannot buy less than 1 item.");
            } else {
                currentQuantity--;
                quantityBaskeView.setText(String.valueOf(currentQuantity));
                float sumItemPrice = currentQuantity * shirt.getPrice();
                String formattedString = String.format("%.02f", sumItemPrice);
                priceBasketView.setText(formattedString);
            }
        }
    }


    public List<ShirtViewModel> fromCursor(Cursor cursor) {

        // Figure out the index of each column
        int idColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME);
        int imageColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_IMAGES);
        int priceColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRICE);
        int sizeColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_SIZE);
        int quantityColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_QUANTITY);
        int supplyNameColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_SUPPLIER_NAME);
        int supplyPhoneNumberColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

        List<ShirtViewModel> shirts = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {

            int currentId = cursor.getInt(idColumnIndex);
            String currentName = cursor.getString(nameColumnIndex);
            byte[] currentImageByteArray = cursor.getBlob(imageColumnIndex);
            float currentPrice = cursor.getFloat(priceColumnIndex);
            int currentSize = cursor.getInt(sizeColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            String currentSupplyName = cursor.getString(supplyNameColumnIndex);
            String currentSupplyPhone = cursor.getString(supplyPhoneNumberColumnIndex);

            shirts.add(new ShirtViewModel(currentId, currentName, currentImageByteArray, currentPrice, currentSize, currentQuantity, currentSupplyName, currentSupplyPhone));
        }

        return shirts;
    }

}
