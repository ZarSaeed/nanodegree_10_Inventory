package com.example.zar.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zar.inventory.data.ProductContract.ProductEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER=1;

    ProductCursorAdapter mCursorAdapter;

    int quntity=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, AddProductActivity.class);
                startActivity(intent);;
            }
        });

        ListView productListView= (ListView) findViewById(R.id.list_view_product);
        View emptyView=findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);


        mCursorAdapter=new ProductCursorAdapter(this,null);
        productListView.setAdapter(mCursorAdapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                final Uri currentProductUri= ContentUris.withAppendedId(ProductEntry.CONTENT_URI,id);


                String[] projection={ProductEntry.COLUMN_PRODUCT_QUANTITY};
                Cursor cursor=getContentResolver().query(currentProductUri,projection,null,null,null);
                quntity=cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY));
                ((Button) view.findViewById(R.id.list_btn_sell)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (quntity<=0)
                        {quntity=0;
                          }
                        else { quntity--;
                       }
                        ContentValues values =new ContentValues();
                        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY,quntity);
                        getContentResolver().update(currentProductUri,values,null,null);


                    }
                });
                Intent intent=new Intent(CatalogActivity.this,DetailsActivity.class);
                intent.setData(currentProductUri);

                startActivity(intent);
            }
        });

       getSupportLoaderManager().initLoader(PRODUCT_LOADER,null,this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            showDeleteConfirmationDailog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projections={ProductEntry._ID,ProductEntry.COLUMN_PRODUCT_NAME,ProductEntry.COLUMN_PRODUCT_PRICE,ProductEntry.COLUMN_PRODUCT_QUANTITY};

        return new CursorLoader(this,ProductEntry.CONTENT_URI,projections,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mCursorAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    mCursorAdapter.swapCursor(null);
    }

    public void showDeleteConfirmationDailog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Delete all products?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

              getContentResolver().delete(ProductEntry.CONTENT_URI,null,null);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog!=null){dialog.dismiss();}
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
}
