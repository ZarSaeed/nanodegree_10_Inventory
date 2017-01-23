package com.example.zar.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zar.inventory.data.ProductContract.ProductEntry;

public class AddProductActivity extends AppCompatActivity {

    EditText productName,productPrice,productQunatity,supplierName;
    Button btnInc,btnDec;
    int quantity=1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        initComponent();
        setOnClick();
    }

    public void initComponent(){
        productName= (EditText) findViewById(R.id.edit_product_name);
        productPrice= (EditText) findViewById(R.id.edit_product_price);
        productQunatity= (EditText) findViewById(R.id.edit_product_quantity);
        supplierName= (EditText) findViewById(R.id.edit_supplier_name);

        btnInc= (Button) findViewById(R.id.btn_inc);
        btnDec= (Button) findViewById(R.id.btn_dec);
    }

    public void setOnClick()
    {
       btnInc.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               quantity++;
               if (quantity>25)
               {quantity--;}
               productQunatity.setText(""+quantity);
           }
       });
        btnDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity--;
                if (quantity<1)
                {quantity++;}
                productQunatity.setText(""+quantity);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {

            saveProduct();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void saveProduct()
    {
        long rowId;

        String nameString=productName.getText().toString().trim();
        Integer priceInt=Integer.parseInt(productPrice.getText().toString());
        Integer quantity=Integer.parseInt(productQunatity.getText().toString());
        String supplier=supplierName.getText().toString();

        ContentValues values=new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME,nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE,priceInt);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY,quantity);
        values.put(ProductEntry.COLUMN_SUPPLIER,supplier);

        rowId= ContentUris.parseId(getContentResolver().insert(ProductEntry.CONTENT_URI,values));

        if (rowId==-1)
        {
            Toast.makeText(this, "insert product failed", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Product save",Toast.LENGTH_SHORT).show();
        }


    }
}
