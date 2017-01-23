package com.example.zar.inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Zar on 1/9/2017.
 */

public class ProductContract {
    //To prevent from someone instantiating the contract class,
    //declered constructor private
    private ProductContract(){}

    /*
    * The "Content authority" is a name for the entire content provider, similar to the
    * relationship between a domain name and its website. A convenient string to use for
    * content authority is the package name for the app, which is guaranteed to unique
    * device.
    * */
    public static final  String CONTENT_AUTHORITY="com.example.zar.inventory";

    /*
    * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    * the content provider.
    * */
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);


    /*
    * Possible path (appended to base content URI for possible URI's)
    * For instance, content://com.example.zar.inventory/product is valid path for
    * looking at pet data. content://com.example.zar.inventory/staff/ will fail,
    * as the ContentProvider hasn't been given any information on what to do with "staff"
    * */

    public static final String PATH_PRODUCTS="products";

    /*
    * Inner class to define table contents
    * */

    public static final class ProductEntry implements BaseColumns{

        //The content URI to access the pet data in the provider

        public static  final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PRODUCTS);

        //Table Name
        public static final String TABLE_NAME="products";

        /*
        * The MIME type of the {@link #CONTENT_URI} for a list of Products.
        * */

        public static final String CONTENT_ITEM_TYPE= ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PRODUCTS;
        public static final String CONTENT_LIST_TYPE=ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PRODUCTS;

        //base column for database to uniquely identify pets
        public static final String _ID=BaseColumns._ID;

        //Column names
        public static final String COLUMN_PRODUCT_NAME="name";
        public static final String COLUMN_PRODUCT_QUANTITY="quantity";
        public static final String COLUMN_PRODUCT_SOLD="sold";
        public static final String COLUMN_PRODUCT_PRICE="price";
        public static final String COLUMN_SUPPLIER="supplier";


    }

}
