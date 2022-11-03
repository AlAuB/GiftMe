package com.example.giftme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wishlistItemsDB";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_WISHLIST_ITEMS = "WishlistItems";
    private static final String ID = "id";
    private static final String itemName = "itemName";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // build sql create statement

        String create_table = "CREATE TABLE " + TABLE_WISHLIST_ITEMS + " ( " + ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + itemName + " TEXT " + " ) ";
        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_WISHLIST_ITEMS);
        // Re-create tables
        onCreate(db);
    }

    public void insert(String item) {
        ContentValues values = new ContentValues();
        values.put(itemName, item);
    }

    public Cursor getAll() {
        String query = "SELECT * FROM " + itemName;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;
        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery(query, null);
        }
        return cursor;
    }


}
