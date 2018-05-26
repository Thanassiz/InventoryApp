package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.StoreContract;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Thanassis on 29/4/2018.
 */

public class ShirtHolder extends RecyclerView.ViewHolder {

    private final String TAG = ShirtHolder.class.getSimpleName();

    @BindView(R.id.shirt_name)
    TextView nameView;
    @BindView(R.id.shirt_checkbox)
    CheckBox checkBox;
    @BindView(R.id.shirt_image)
    ImageView imageView;
    @BindView(R.id.shirt_price)
    TextView priceView;
    @BindView(R.id.shirt_size)
    TextView sizeView;
    @BindView(R.id.shirt_available)
    TextView availableView;
    @BindView(R.id.shirt_supplier)
    TextView supplierView;

    private Context context;
    public ShirtHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.context = context;
    }

    public void bindView( final ShirtViewModel shirt ) {

        nameView.setText(shirt.getName());
        priceView.setText(String.valueOf(shirt.getPrice()));
        if (shirt.getSize() == 0){
            sizeView.setText(R.string.size_XS);
        }else if (shirt.getSize() == 1){
            sizeView.setText(R.string.size_S);
        }else if (shirt.getSize() == 2){
            sizeView.setText(R.string.size_M);
        }
        else if (shirt.getSize() == 3){
            sizeView.setText(R.string.size_L);
        }
        else if (shirt.getSize() == 4){
            sizeView.setText(R.string.size_XL);
        }else {
            sizeView.setText(R.string.unknown_size);
        }

        supplierView.setText(shirt.getSupplierName());
        imageView.setImageBitmap(Utilities.getBitmap(shirt.getImages()));
        if (shirt.getQuantity() == 0){
            availableView.setText(R.string.available_stock_no);
        }else {
            availableView.setText(R.string.available_stock_yes);
        }
        checkBox.setOnCheckedChangeListener(null); //  This is the proper way to handle onChecked events.
        checkBox.setChecked(shirt.isChecked);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ShirtAdapter.shirtMap.get( shirt.getId() ).isChecked = b;
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create new intent to go to {@link ProductActivity}
                Intent intent = new Intent(context ,ProductActivity.class);
                // Form the content URI that represents the specific list item that was clicked on,
                // by appending the "id" onto the {@link StoreEntry#CONTENT_URI}.
                // Example => content://com.example.android.inventoryapp/shirts/2, for product id = 2
                long id =  shirt.getId();
                Uri currentProductUri = ContentUris.withAppendedId(StoreContract.StoreEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                intent.setData(currentProductUri);
                // Launch the {@link ProductActivity} to display the data for the current item
                Log.e(TAG, "item clicked in position: " + getAdapterPosition() + " , with id: " + id);
                context.startActivity(intent);
            }
        });
    }

}
