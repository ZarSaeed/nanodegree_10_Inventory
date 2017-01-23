package com.example.zar.inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import com.example.zar.inventory.data.ProductContract.ProductEntry;

/**
 * Created by Zar on 1/9/2017.
 */

public class ProductProvider extends ContentProvider {

    //Reference to PetDbHelper class
    private ProductDbHelper mDbHelper;

    /**URI matcher code for the content URI for the pets table */
    private static final int PRODUCTS=100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PRODUCT_ID=101;

    /*
    * UriMatcher object to match a content URI to a corresponding code.
    * The input passed into the constructor represents the code to return for the root URI.
    * It's common to use NO_MATCH as the input for this case.
    * */
    private static final UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    //Static initializer. This is run the first time anything is called from this class.
    static{
        //The calls to addURI() go here,for all of the content URI patterns that the provider
        //should recognize. All paths added to the UriMatcher have a corresponding code
        //when a match is found.
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PATH_PRODUCTS,PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PATH_PRODUCTS+"/#",PRODUCT_ID);
    }

    /** Tag for the log messages */
    public static final String LOG_TAG=ProductProvider.class.getSimpleName();

    /*
    * Initialize the provider and the database helper object.
    * */
    @Override
    public boolean onCreate(){
        //Make sure the variable is a global variable, so it can be referenced from other
        //ContentProvider methods.
        mDbHelper=new ProductDbHelper(getContext());
        Log.d(LOG_TAG,"database created");
        return true;
    }
    /*
    * Perform the query for the given URI. Use the given projection, selection ,selection arguments
    * */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,String sortOrder)
    {
        //Get readable database
        SQLiteDatabase database=mDbHelper.getReadableDatabase();

        //This cursor will hold the result of the query
        Cursor cursor;
        int match=sUriMatcher.match(uri);
        Log.d(LOG_TAG,String.valueOf(match));
        switch (match)
        {
            case PRODUCTS:
                //For the Products code, query the pets table directly with the given
                //projection,selection,selection arguments, and sort order. The cursor
                //could contain multiple rows of the pets table.
                cursor=database.query(ProductContract.ProductEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
            break;
            case PRODUCT_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.

                selection= ProductContract.ProductEntry._ID+ "=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor=database.query(ProductContract.ProductEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI"+uri);
        }
        //set the Notification uri on the cursor
        //so that we know if data changes we need to update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return  cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match=sUriMatcher.match(uri);
        switch (match)
        {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unkown URI "+uri+"with match"+match);
        }
    }

    @Override
    public int delete(Uri uri,String selection,String[] selectionArgs)
    {
        //Get writable database
        SQLiteDatabase database=mDbHelper.getWritableDatabase();
        final int match=sUriMatcher.match(uri);
        //Track the number of rows that were deleted
        int rowsDeleted;
        switch (match)
        {
            case PRODUCTS:
                //Delete all rows that match the selection and selection args
                rowsDeleted=database.delete(ProductEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case PRODUCT_ID:
                //Delete single row given by the ID in the URI
                selection =ProductEntry._ID + "=?";
                selectionArgs=new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted=database.delete(ProductEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for "+uri);
        }
        if (rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues)
    {
        final int match=sUriMatcher.match(uri);
        switch (match)
        {
            case PRODUCTS:
                return inserPet(uri,contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for"+ uri);
        }
    }



    public Uri inserPet(Uri uri,ContentValues contentValues)
    {
        //check that the name is not null
        String name=contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if(name==null)
        {
            throw new IllegalArgumentException("Product requires a name");
        }
        //check that quantity is not null and valid from three constants
        Integer quantity=contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity==null)
        {
            throw new IllegalArgumentException("Product requires quantity");
        }
        Integer price=contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price==null)
        {
            throw new IllegalArgumentException("Product requires price");
        }
        SQLiteDatabase db=mDbHelper.getWritableDatabase();

        long id =db.insert(ProductEntry.TABLE_NAME,null,contentValues);

        //Notify all listeners that the data has changed for PET_CONTENT uri
        getContext().getContentResolver().notifyChange(uri,null);

        //Once we know the ID of the new row in the table,
        //return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri,id);
    }
    /*
    * Updates the data at given selection arguments, with the new ContentValues.
    * */
    @Override
    public  int update(Uri uri,ContentValues contentValues,String selection,String[] selectionArgs)
    {
        final  int match=sUriMatcher.match(uri);
        switch (match)
        {
            case PRODUCTS:
                return updateProduct(uri,contentValues,selection,selectionArgs);
            case PRODUCT_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection=ProductEntry._ID+ "=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri,contentValues,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for "+uri);
        }
    }
    /*
    *  Update products in the database with the given content values.apply the changes to the rows
    * specified in the selection and selection arguments(which could be 0 or 1 or more Products.
    * Return the number of rows that were successfully updated.
    * */
    private int updateProduct(Uri uri,ContentValues values,String selection,String[] selectionArgs)
    {
        //if the{@link ProductEntry#COLUMN_PET_NAME} key is present,
        //check that the name value is not null.
        if(values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME))
        {
            String name=values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name==null)
            {
                throw new IllegalArgumentException("Product requires a name");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE))
        {
            Integer integer=values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (integer==null)
            {
                throw new IllegalArgumentException("Product requires a price");
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY))
        {
            Integer quantity=values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity==null)
            {
                throw new IllegalArgumentException("Product requires quatity");
            }
        }

        if (values.size()==0)
        {
            return 0;
        }

        SQLiteDatabase db=mDbHelper.getWritableDatabase();

        int rowsUpdated=db.update(ProductEntry.TABLE_NAME,values,selection,selectionArgs);

        if (rowsUpdated!=0)
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }

}
