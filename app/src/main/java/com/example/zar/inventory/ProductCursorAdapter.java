package com.example.zar.inventory;

import android.content.Context;
import android.database.Cursor;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.zar.inventory.data.ProductContract.ProductEntry;

import org.w3c.dom.Text;

/**
 * Created by Zar on 1/9/2017.
 */

public class ProductCursorAdapter extends CursorAdapter{

    public ProductCursorAdapter(Context context,Cursor cursor)
    {
        super(context,cursor,0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_list,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView name= (TextView) view.findViewById(R.id.list_txt_name);
        TextView price= (TextView) view.findViewById(R.id.list_txt_price);
        TextView quantity= (TextView) view.findViewById(R.id.list_txt_quantity);

        String nameString=cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME));
        String priceString=cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE));
        String quantityString=cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY));

        name.setText(nameString);
        price.setText(priceString);
        quantity.setText(quantityString);

    }
}
