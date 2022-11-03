package com.example.giftme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

    private final Context context;

    private static final String DATABASE_NAME = "WISHLIST_DB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "COLLECTIONS";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "NAME";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_table = "CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT " + " ) ";
        sqLiteDatabase.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /**
     * Create a new Table when add a new collection.
     * The Table will always have: Name, Price, Hearts, Date, Image
     *
     * @param table_name Name for that Collection
     */
    public void createNewTable(String table_name) {
        Toast.makeText(context, "This function is NOT implemented", Toast.LENGTH_SHORT).show();
    }

    /**
     * Read all the data from a specific table
     *
     * @param tableName The table's name you want to read data from
     * @return Cursor
     */
    public Cursor readCollectionTableAllData(String tableName) {
        String query = "SELECT * FROM " + tableName;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;
        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery(query, null);
        }
        return cursor;
    }

    /**
     * Add new collection in the Collection table
     *
     * @param name collection name
     */
    public void addNewCollection(String name) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        long status = database.insert(TABLE_NAME, null, values);
        if (status == -1) {
            Toast.makeText(context, "Insert failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Insert Success", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Delete specific data in specific table
     *
     * @param rowId id for that item in that table
     */
    public void deleteData(String rowId, String tableName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        long status = sqLiteDatabase.delete(tableName, "id=?", new String[]{rowId});
        if (status == -1) {
            Toast.makeText(context, "Cannot delete", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Delete success", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Delete a whole table
     *
     * @param tableName The table's name which you want to delete
     */
    public void deleteTable(String tableName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    /**
     * Delete all data in that Table
     */
    public void deleteAll(String tableName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + tableName);
    }
}
