package com.example.zar.inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.zar.inventory.data.ProductContract.ProductEntry;

/**
 * Created by Zar on 1/9/2017.
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    //Log tag
    public static final String LOG_TAG=ProductDbHelper.class.getSimpleName();

    //if we change database schema we need to change the database version
    private static final int DATABASE_VERSION=1;

    //Database name
    private static final String DATABASE_NAME="inventory.db";


    public ProductDbHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Sql query to create table
        final String SQL_CREATE_PRODUCT_TABLE ="CREATE TABLE "+ ProductEntry.TABLE_NAME+ "("
                +ProductEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +ProductEntry.COLUMN_PRODUCT_NAME+ " TEXT NOT NULL,"
                +ProductEntry.COLUMN_PRODUCT_PRICE+ " REAL NOT NULL, "
                +ProductEntry.COLUMN_PRODUCT_QUANTITY+ " INTEGER NOT NULL,"
                +ProductEntry.COLUMN_PRODUCT_SOLD+ " INTEGER NOT NULL DEFAULT 0,"
                +ProductEntry.COLUMN_SUPPLIER+" TEXT NOT NULL"+ " );";
        Log.d(LOG_TAG,"onCreate: "+SQL_CREATE_PRODUCT_TABLE);
        db.execSQL(SQL_CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ProductEntry.TABLE_NAME);
        onCreate(db);
    }
}
