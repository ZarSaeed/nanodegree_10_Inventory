package com.example.zar.inventory;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zar.inventory.data.ProductContract.ProductEntry;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAILS_PRODUCT_LOADER=2;

    private Uri currentProductUri;
    private Intent intent;
    TextView nameText,priceText,quantityText;
    Button deleteBtn,orderBtn,incBtn,decBtn;
    int quantity=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        intent=getIntent();
        currentProductUri=intent.getData();
        if (currentProductUri!=null)
        {
            getSupportLoaderManager().initLoader(DETAILS_PRODUCT_LOADER,null,this);
        }
        intiComponent();
        setClickListner();
    }
    public void intiComponent()
    {
        nameText= (TextView) findViewById(R.id.details_txt_product_name);
        priceText= (TextView) findViewById(R.id.details_txt_product_price);
        quantityText= (TextView) findViewById(R.id.details_product_quantity);

        deleteBtn= (Button) findViewById(R.id.details_btn_delete);
        orderBtn= (Button) findViewById(R.id.details_btn_order);
        incBtn= (Button) findViewById(R.id.details_btn_inc);
        decBtn= (Button) findViewById(R.id.details_btn_dec);
    }

    public void setClickListner()
    {
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT,"Product Order for :"+nameText.getText().toString());
                intent.putExtra(Intent.EXTRA_TEXT,createStringData());
                if (intent.resolveActivity(getPackageManager())!=null)
                {
                    startActivity(intent);
                }
            }
        });

        incBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                quantity++;
                if (quantity>25)
                {quantity--;}
                quantityText.setText(""+quantity);
            }
        });
        decBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity--;
                if (quantity<1)
                {
                    quantity++;
                }
                quantityText.setText(""+quantity);
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePoduct();
            }
        });
    }

    public String createStringData()
    {
        String data="Product Name : "+nameText.getText().toString().trim()+"\n " +
                "Product Price : "+priceText.getText().toString().trim()+"\n" +
                "Product Quantity : "+quantityText.getText().toString().trim();
        return data;
    }
    public void deletePoduct(){
        if(currentProductUri!=null){
            getContentResolver().delete(currentProductUri,null,null);
            Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Error with deleting product",Toast.LENGTH_SHORT).show();
        }
        finish();
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this,currentProductUri,null,null,null,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToNext())
        {
            String name=data.getString(data.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME));
            int price=data.getInt(data.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE));

            nameText.setText(name);
            priceText.setText(""+price);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameText.setText("");
        priceText.setText("");
        quantityText.setText("");
    }
}
