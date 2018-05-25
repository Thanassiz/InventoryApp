package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.inventoryapp.data.StoreContract;

import java.util.HashMap;

/**
 * Created by Thanassis on 29/4/2018.
 */
public class ShirtAdapter extends RecyclerViewCursorAdapter< ShirtHolder >
{
	private Context context;
	private Cursor cursor;
	public static HashMap< Integer, ShirtViewModel > shirtMap = new HashMap<>();

	public ShirtAdapter( Context context, Cursor cursor )
	{
		super( context, cursor );
		this.context = context;
		this.cursor = cursor;
		shirtMap.clear();
	}

	@Override
	public ShirtHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType )
	{
		View view = LayoutInflater.from( context ).inflate( R.layout.shirt_recycler_layout, parent, false );
		return new ShirtHolder( view, context );
	}

	@Override
	public void onBindViewHolder( final ShirtHolder viewHolder, final Cursor cursor, final int position )
	{
		int idColumnIndex = cursor.getColumnIndex( StoreContract.StoreEntry._ID );
		int currentId = cursor.getInt( idColumnIndex );
		if ( !shirtMap.containsKey( currentId ) )
		{
			int nameColumnIndex = cursor.getColumnIndex( StoreContract.StoreEntry.COLUMN_PRODUCT_NAME );
			int imageColumnIndex = cursor.getColumnIndex( StoreContract.StoreEntry.COLUMN_IMAGES );
			int priceColumnIndex = cursor.getColumnIndex( StoreContract.StoreEntry.COLUMN_PRICE );
			int sizeColumnIndex = cursor.getColumnIndex( StoreContract.StoreEntry.COLUMN_SIZE );
			int quantityColumnIndex = cursor.getColumnIndex( StoreContract.StoreEntry.COLUMN_QUANTITY );
			int supplyNameColumnIndex = cursor.getColumnIndex( StoreContract.StoreEntry.COLUMN_SUPPLIER_NAME );
			int supplyPhoneNumberColumnIndex = cursor.getColumnIndex( StoreContract.StoreEntry.COLUMN_SUPPLIER_PHONE_NUMBER );

			String currentName = cursor.getString( nameColumnIndex );
			byte[] currentImageByteArray = cursor.getBlob( imageColumnIndex );
			float currentPrice = cursor.getFloat( priceColumnIndex );
			int currentSize = cursor.getInt( sizeColumnIndex );
			int currentQuantity = cursor.getInt( quantityColumnIndex );
			String currentSupplyName = cursor.getString( supplyNameColumnIndex );
			String currentSupplyPhone = cursor.getString( supplyPhoneNumberColumnIndex );
			shirtMap.put( currentId, new ShirtViewModel( currentId, currentName, currentImageByteArray, currentPrice, currentSize, currentQuantity, currentSupplyName, currentSupplyPhone ) );
		}
		viewHolder.bindView( shirtMap.get( currentId ) );
	}
}
