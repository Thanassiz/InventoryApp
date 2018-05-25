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
import java.util.HashMap;
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
    public static HashMap<Integer, ShirtViewModel> basketMap = new HashMap<>();

    /***** Creating OnItemClickListener *****/

    // Define listener member variable
    private OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public CheckOutShirtAdapter(Context context, Cursor cursor, ArrayList<String> selectedItems) {
        super(context, cursor);
        this.context = context;
        this.cursor = cursor;
        this.selectedItems = selectedItems;
        this.basketMap.clear();
    }


    @Override
    public BasketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shirt_shop_recycler_layout, parent, false);
        return new BasketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BasketViewHolder viewHolder, Cursor cursor, int position) {
        ShirtViewModel shirt = fromCursor(cursor);
        viewHolder.bindBasketView(shirt, selectedItems);
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
        TextView quantityBasketView;
        @BindView(R.id.shop_price_value)
        TextView priceBasketView;
        private ShirtViewModel shirt;

        BasketViewHolder(View basketItemView) {
            super(basketItemView);
            ButterKnife.bind(this, basketItemView);
        }

        public void bindBasketView(final ShirtViewModel shirt, ArrayList<String> selectedItems) {
            this.shirt = shirt;
            for (String selectedId : selectedItems) {
                if (selectedId.equals(String.valueOf(shirt.getId()))) {
                    nameItemView.setText(shirt.getName());
                    sizeItemView.setText(String.valueOf(shirt.getSize()));
                    quantityItemView.setText(String.valueOf(shirt.getQuantity()));
                }
            }
            quantityBasketView.setText(String.valueOf(0));
            priceBasketView.setText(String.valueOf(0.00));

            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                            quantityMinusValidation(Integer.parseInt(quantityBasketView.getText().toString()), shirt);
                        }
                    }
                }
            });

            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {

                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                            quantityPlusValidation(Integer.parseInt(quantityBasketView.getText().toString()), Integer.parseInt(quantityItemView.getText().toString()), shirt);
                        }
                    }
                }
            });

        }

      /*
       @OnClick(R.id.shop_plus)
        void onClickPlus() {
            if (shirt != null) {
                quantityPlusValidation(Integer.parseInt(quantityBasketView.getText().toString()), Integer.parseInt(quantityItemView.getText().toString()), shirt);
            }
        }

        @OnClick(R.id.shop_minus)
        void onClickMinus() {
            if (shirt != null) {
                quantityMinusValidation(Integer.parseInt(quantityBasketView.getText().toString()), shirt);
            }
        }
        */

        public void quantityPlusValidation(int currentQuantity, int itemQuantity, ShirtViewModel shirt) {
            if (currentQuantity >= itemQuantity) {
                Log.i(TAG, "Not enough items on stock.");
            } else {
                currentQuantity++;
                quantityBasketView.setText(String.valueOf(currentQuantity));
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
                quantityBasketView.setText(String.valueOf(currentQuantity));
                float sumItemPrice = currentQuantity * shirt.getPrice();
                String formattedString = String.format("%.02f", sumItemPrice);
                priceBasketView.setText(formattedString);
            }
        }

    }


    public ShirtViewModel fromCursor(Cursor cursor) {

        int idColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry._ID);
        int currentId = cursor.getInt(idColumnIndex);
        if (!basketMap.containsKey(currentId)) {
            // Figure out the index of each column
            int nameColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRODUCT_NAME);
            int imageColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_IMAGES);
            int priceColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRICE);
            int sizeColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_SIZE);
            int quantityColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_QUANTITY);
            int supplyNameColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_SUPPLIER_NAME);
            int supplyPhoneNumberColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER);


            String currentName = cursor.getString(nameColumnIndex);
            byte[] currentImageByteArray = cursor.getBlob(imageColumnIndex);
            float currentPrice = cursor.getFloat(priceColumnIndex);
            int currentSize = cursor.getInt(sizeColumnIndex);
            int currentQuantity = cursor.getInt(quantityColumnIndex);
            String currentSupplyName = cursor.getString(supplyNameColumnIndex);
            String currentSupplyPhone = cursor.getString(supplyPhoneNumberColumnIndex);

            basketMap.put(currentId, new ShirtViewModel(currentId, currentName, currentImageByteArray, currentPrice, currentSize, currentQuantity, currentSupplyName, currentSupplyPhone));
        }

        ShirtViewModel shirt = basketMap.get(currentId);
        return shirt;
    }

}
