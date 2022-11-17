package com.example.giftme;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DataBaseHelper extends SQLiteOpenHelper {

    private final Context context;

    //other
    private static final String DATABASE_NAME = "WISHLIST_DB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "COLLECTIONS";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "NAME";
    private static final String FIRESTORE_ID = "firestore_id";
    private final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private static final String TAG = "DataBaseHelper debug::";
    private static final String[] uniqueId = // should be changed to the id of logged in users, this is just for testing
            {
                    "lesleychen456@gmail.com",
                    "lyujin@bu.edu",
                    "sj0726@bu.edu",
                    "tg757898305@gmail.com",
                    "tchen556@gmail.com",
                    "wycalex@bu.edu"
            };
    private static final int random = new Random().nextInt(uniqueId.length);

    //for ITEMS
    private static final String ITEM_ID = "ITEM_ID";
    private static final String ITEM_NAME = "ITEM_NAME";
    private static final String ITEM_HEARTS = "ITEM_HEARTS";
    private static final String ITEM_PRICE = "ITEM_PRICE";
    private static final String ITEM_DESCRIPTION = "ITEM_DESCRIPTION";
    private static final String ITEM_DATE = "ITEM_DATE";
    private static final String ITEM_IMAGE = "ITEM_IMAGE";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_table = "CREATE TABLE " + TABLE_NAME + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                FIRESTORE_ID + " TEXT " + " ) ";
        sqLiteDatabase.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /**
     * Create a new Table when adding a new collection
     * this table will contain the items user adds to the collection
     */
    public void createNewTable(String table_name) {
        SQLiteDatabase database = this.getWritableDatabase();
        String create_table = "CREATE TABLE " + "'" + table_name + "'" + " ( " +
                ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ITEM_NAME + " TEXT, " +
                ITEM_HEARTS + " INTEGER, " +
                ITEM_PRICE + " INTEGER, " +
                ITEM_DESCRIPTION + " TEXT, " +
                ITEM_DATE + " TEXT, " +
                ITEM_IMAGE + " INTEGER, " +
                FIRESTORE_ID + " TEXT " +" ) "; //redundant???
        database.execSQL(create_table);
    }

    /**
     * insert (new) item to [Collection] table
     * **/

    public void insertItemIntoCollection(String collection, Item item){
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlInsert = "insert into " + "'" + collection + "'";
        sqlInsert += " values( null, '" + item.getName()
                + "', '" + item.getHearts()
                + "', '" + item.getPrice()
                + "', '" + item.getDescription()
                + "', '" + item.getDate()
                + "', '" + item.getImg()
                + "', '" +  item.getTableID() + "' )";
        db.execSQL(sqlInsert);
//        db.close();
    }

    /**
     * get all items from a collection table
     *
     */
    public ArrayList<Item> selectAll(String collectionName) {
        String sqlQuery = "select * from " + "'" + collectionName + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);

        ArrayList<Item> items = new ArrayList<Item>();
        while (cursor.moveToNext()) {
            //Item(int newId, String newName, int newHearts, int newPrice,
            // String newDescription, String newDate, int newImg,  int newTableID)
            Item currentItem
                    = new Item(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    Integer.parseInt(cursor.getString(2)),
                    Integer.parseInt(cursor.getString(3)),
                    cursor.getString(4),
                    cursor.getString(5),
                    Integer.parseInt(cursor.getString(6)),
                    cursor.getString(7)
                    );
            items.add(currentItem);
        }
//        db.close();
        return items;
    }

    /**
     *  update item in database
     */
    //add link later
    public void updateById(String collection_name, int id, String name, int price, String description,
                           int hearts, int img, String fireStoreId){
        SQLiteDatabase db = this.getWritableDatabase();

        String sqlUpdate = "update " + "'" + collection_name + "'"
                + " set " + ITEM_NAME + " = '" + name + "', "
                + ITEM_HEARTS + "= '" + hearts + "', "
                + ITEM_PRICE + "= '" + price + "', "
                + ITEM_DESCRIPTION + "= '" + description + "', "
                + ITEM_IMAGE + "= '" + img + "', "
                + FIRESTORE_ID + "= '" + fireStoreId + "' "
                +  "where " + ITEM_ID + "= " + id;

        db.execSQL(sqlUpdate);
//        db.close();
    }

    /**
     * Read all the data from a specific table
     * @param tableName The table's name you want to read data from
     * @return Cursor
     */
    public Cursor readCollectionTableAllData(String tableName) {
        String query = "SELECT * FROM " + "'" + tableName + "'";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = null;
        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery(query, null);
        }
        return cursor;
    }

    /**
     * Add new collection in the Collection table
     * @param name collection name
     */

    public void addNewCollection(String name) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // add to firestore first to make sure the collection is created
        // for now, get random unique id from the array above and check if the document's been created in firestore
        Map<String, Object> wishlist = new HashMap<>();

        DocumentReference userDocIdRef = fireStore.collection("usersTest").document(uniqueId[random]);
        DocumentReference wishlistDocIdRef = userDocIdRef.collection("wishlists").document();
        wishlistDocIdRef.set(wishlist)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written! (ID: " + wishlistDocIdRef.getId() + ")");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        values.put(COLUMN_NAME, name);
        values.put(FIRESTORE_ID, wishlistDocIdRef.getId());
        long status = database.insert(TABLE_NAME, null, values);
        if (status == -1) {
            Toast.makeText(context, "Insert failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Insert Success", Toast.LENGTH_SHORT).show();
        }
    }

    //????
    public String getData(String rowId, String tableName) {
        // still using rowId to fetch data.. should be changed to either id or firestore_id
        String query = "SELECT * FROM " + "'" + tableName + "'" + " WHERE " + COLUMN_ID + " = " + rowId;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor;
        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(FIRESTORE_ID);
                return cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return "FAILED";
    }

    /**
     * Delete specific data in specific table
     * @param rowId id for that item in that table
     */
    public void deleteData(String rowId, String tableName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String firestoreId = getData(rowId, tableName);
        Log.d(TAG, "deleteData: " + firestoreId);
        long status = sqLiteDatabase.delete(tableName, FIRESTORE_ID + "=?", new String[]{firestoreId});
        if (status == -1) {
            Toast.makeText(context, "Cannot delete", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Delete success", Toast.LENGTH_SHORT).show();
            // after successful delete in local db, delete in firestore as well
            fireStore.collection("usersTest").document(uniqueId[random]).collection("wishlists").document(firestoreId).delete()
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
        }
    }

    /**
     * Delete a whole table
     * @param tableName The table's name which you want to delete
     */
    public void deleteTable(String tableName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "'" + tableName + "'");
    }

    /**
     * Delete all data in that Table
     */
    public void deleteAll(String tableName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + "'" + tableName + "'");
    }
}
