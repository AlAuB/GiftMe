package com.example.giftme.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataBaseHelper extends SQLiteOpenHelper {

    private final Context context;
    private static String userEmail;

    //other
    private static final String DATABASE_NAME = "WISHLIST_DB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "COLLECTIONS";
    private static final String FRIEND_ID = "FRIEND_ID";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "COLLECTION_NAME";
    private static final String CLAIMED = "CLAIMED";
    private static final String USER_NAME = "USER_NAME";
    private static final String PROFILE_IMAGE = "PROFILE_IMAGE";

    private static final String FIRESTORE_ID = "firestore_id";
    private final FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final String COLLECTIONS_USERS = "users"; //this should be changed to 'users'
    private static final String COLLECTIONS_WISHLISTS = "wishlists";


    private static final String TAG = "DataBaseHelper debug::";

    //for ITEMS
    private static final String ITEM_ID = "ITEM_ID";
    private static final String ITEM_URL = "ITEM_URL";
    private static final String ITEM_NAME = "ITEM_NAME";
    private static final String ITEM_HEARTS = "ITEM_HEARTS";
    private static final String ITEM_PRICE = "ITEM_PRICE";
    private static final String ITEM_DESCRIPTION = "ITEM_DESCRIPTION";
    private static final String ITEM_DATE = "ITEM_DATE";
    private static final String ITEM_IMAGE = "ITEM_IMAGE";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        userEmail = SessionManager.getUserEmail(context);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String create_table = "CREATE TABLE " + TABLE_NAME + " ( " +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                USER_NAME + " TEXT, " +
                FRIEND_ID + " TEXT, " +
                PROFILE_IMAGE + " TEXT, " +
                FIRESTORE_ID + " TEXT UNIQUE" + " ) ";
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
                ITEM_URL + " TEXT, " +
                ITEM_NAME + " TEXT, " +
                ITEM_HEARTS + " REAL, " +
                ITEM_PRICE + " INTEGER, " +
                ITEM_DESCRIPTION + " TEXT, " +
                ITEM_DATE + " TEXT, " +
                ITEM_IMAGE + " INTEGER, " +
                CLAIMED + " INTEGER, " +
                FIRESTORE_ID + " TEXT " +" ) ";
        database.execSQL(create_table);
    }

    public void setUserEmail(String email) {
        userEmail = email;
    }

    /**
     * Add a new collection to the database
     * @param email the email of the user
     * @param displayName the name of the user
     * @param photoUrl the url of the user's profile picture
     */
    public void createUser(String email, String displayName, String photoUrl) {
        setUserEmail(email);
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("displayName", displayName);
        user.put("photoUrl", photoUrl);
        user.put("friends", new ArrayList<String>());
        Log.d(TAG, "createUser: " + email + " " + displayName + " " + photoUrl);

        fireStore.collection("users").document(email).set(user, SetOptions.merge()).addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: user " + email + " created")).addOnFailureListener(e -> Log.d(TAG, "onFailure: user " + email + " " + e.getMessage()));
    }

    /**
     * callback interface for checkUserExists
     */
    public interface UserExists {
        void onCallback(boolean exists);
    }

    /**
     * check if the user exists in the database
     * @param email the email of the user
     * @param userExists the callback function
     */
    public void checkUserExists(String email, UserExists userExists) {
        DocumentReference docRef = fireStore.collection("users").document(email);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "User exists: " + document.getData());
                    userExists.onCallback(true);
                } else {
                    Log.d(TAG, "User doesn't exist");
                    userExists.onCallback(false);
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    /**
     * insert (new) item to [Collection] table
     * **/
    public void insertItemIntoCollection(String collection, Item item){
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlInsert = "insert into " + "'" + collection + "'";
        sqlInsert += " values( null, '" + item.getWebsite()
                + "', '" + item.getName()
                + "', '" + item.getHearts()
                + "', '" + item.getPrice()
                + "', '" + item.getDescription()
                + "', '" + item.getDate()
                + "', '" + item.getImg()
                + "', '" + item.getClaimed()
                + "', '" +  item.getFireStoreID() + "' )";
        Log.d(TAG, "insertItemIntoCollection: " + sqlInsert);
        db.execSQL(sqlInsert);

        // generate a map object to put into firestore
        Map<String, Object> firestoreItem = convertItemIntoMap(item);

        // find the firestore_id of the collection in sqlite
        String sqlSelect =
                "select " + FIRESTORE_ID + " from "
                + TABLE_NAME + " where "
                + COLUMN_NAME + " = '" + collection + "'";
        Cursor cursor = db.rawQuery(sqlSelect, null);
        if (cursor.moveToFirst()) {
            Log.d(TAG, "insertItemIntoCollection: " + cursor.getString(0) + " " + userEmail);
            DocumentReference userDocIdRef = fireStore.collection("users").document(userEmail);
            DocumentReference collectionDocIdRef = userDocIdRef.collection("wishlists").document(cursor.getString(0));
            collectionDocIdRef.set(firestoreItem, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
        }
        cursor.close();
    }

    public Map<String, Object> convertItemIntoMap(Item item){
        Map<String, Object> itemMap = new HashMap<>();
        Map<String, Object> nestedItemMap = new HashMap<>();
        itemMap.put("name", item.getName());
        itemMap.put("hearts", item.getHearts());
        itemMap.put("price", item.getPrice());
        itemMap.put("description", item.getDescription());
        itemMap.put("date", item.getDate());
        itemMap.put("img", item.getImg());
        itemMap.put("claimed", item.getClaimed());
        Log.d(TAG, "convertItemIntoMap: " + item.getFireStoreID());
        nestedItemMap.put(item.getFireStoreID(), itemMap);
        return nestedItemMap;
    }

    public Item convertMapIntoItem(Map<String, Object> map,  String itemID){
        Item item = new Item();
        item.setName(String.valueOf(map.get("name")));
        item.setHearts(((Double)map.get("hearts")).floatValue());
        item.setPrice(Math.toIntExact(((Long) map.get("price"))));
        item.setDescription((String) map.get("description"));
        item.setDate((String) map.get("date"));
        item.setImg((String) map.get("image"));
        item.setClaimed((Boolean) map.get("claimed"));
        item.setKnownFireStoreID(itemID);
        //        item.setKnownTableID(wishlistID);
        return item;
    }

//    public List<Item> getAllItemsFromDatabase
//

    /**
     * update Claimed status in FireStore for friend items
     */
    public void editClaimed(String userID, String collectionID, String itemID, Boolean isClaimed) {
        DocumentReference userRef = fireStore.collection(COLLECTIONS_USERS).document(userID);
        DocumentReference collectionRef = userRef.collection(COLLECTIONS_WISHLISTS).document(collectionID);

        String field = itemID + ".claimed";
        collectionRef.update(field, isClaimed)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentReference update claim (user: " + userID + collectionID + itemID + ")"))
                .addOnFailureListener(e -> Log.w(TAG, "Error claiming item", e));
    }


    /**
     * get all items from a collection table
     *
     */
    public Cursor selectAll(String collectionName) {
        String sqlQuery = "select * from " + "'" + collectionName + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(sqlQuery, null);
        }
        return cursor;
    }

    /**
     *  update item in database
     */
    //add link later
    public void updateById(String collection_name, String url, int id, String name, int price, String description,
                           int hearts, String img, String fireStoreId){
        SQLiteDatabase db = this.getWritableDatabase();

        String sqlUpdate = "update " + "'" + collection_name + "'"
                + " set " + ITEM_NAME + " = '" + name + "', "
                + ITEM_HEARTS + "= '" + hearts + "', "
                + ITEM_URL + "= '" + url + "', "
                + ITEM_PRICE + "= '" + price + "', "
                + ITEM_DESCRIPTION + "= '" + description + "', "
                + ITEM_IMAGE + "= '" + img + "', "
                + FIRESTORE_ID + "= '" + fireStoreId + "' "
                +  "where " + ITEM_ID + "= " + id;

        db.execSQL(sqlUpdate);
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

    public void addFriend(String friendId) {
        Log.d(TAG, "addFriend: " + userEmail + " " + friendId);
//        Map<String, Object> friend = new HashMap<>();
        DocumentReference userDocIdRef = fireStore.collection("users").document(userEmail);
        userDocIdRef.update("friends", FieldValue.arrayUnion(friendId));
    }

    public void getFriends() {
        DocumentReference userDocIdRef = fireStore.collection("users").document(userEmail);
        ArrayList<String> friends = new ArrayList<>();
        userDocIdRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    /**
     * Add new collection in the Collection table
     * @param userName etc
     */

    // TODO:: create another function for adding new collection to COLLECTIONS database and firestore when it's a friend's wishlist


    public void addNewCollection(String userName, String collectionName, String friendID) {
    
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // add to firestore first to make sure the collection is created
        // for now, get random unique id from the array above and check if the document's been created in firestore
        Map<String, Object> wishlist = new HashMap<>();
        wishlist.put("Collection Name", collectionName);
        wishlist.put("Friend ID", friendID);

        DocumentReference userDocIdRef = fireStore.collection("users").document(userEmail);
        DocumentReference wishlistDocIdRef = userDocIdRef.collection("wishlists").document();
        wishlistDocIdRef.set(wishlist)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written! (ID: " + wishlistDocIdRef.getId() + ", user:" + userEmail + ")"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

        values.put(COLUMN_NAME, collectionName);
        values.put(USER_NAME,  userName);
        values.put(FIRESTORE_ID, wishlistDocIdRef.getId());
        values.putNull(FRIEND_ID);
        values.putNull(PROFILE_IMAGE); //user does have a profile image but don't really need it.

        long status = database.insert(TABLE_NAME, null, values);
        if (status == -1) {
            Toast.makeText(context, "Insert failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Insert Success", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * for adding friend's collections
     * @param friendName String
     * @param collectionName String
     * @param friendID String
     * @param fsID String
     */
    public void addNewFriendCollection(String friendName, String collectionName, String friendID, String fsID, String pfp) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // add to firestore first to make sure the collection is created
        // for now, get random unique id from the array above and check if the document's been created in firestore
        Map<String, Object> wishlist = new HashMap<>();
        wishlist.put("Collection Name", collectionName);
        wishlist.put("Friend ID", friendID);
        //need to add other fields to firestore

        DocumentReference userDocIdRef = fireStore.collection("users").document(friendID);
        DocumentReference wishlistDocIdRef = userDocIdRef.collection("wishlists").document();
        wishlistDocIdRef.set(wishlist)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written! (ID: " + wishlistDocIdRef.getId() + ", user:" + userEmail + ")"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

        values.put(USER_NAME, friendName);
        values.put(COLUMN_NAME, collectionName);
        values.put(FIRESTORE_ID, fsID);
        values.put(FRIEND_ID, friendID);
        values.put(PROFILE_IMAGE, pfp);

        long status = database.insert(TABLE_NAME, null, values);
        if (status == -1) {
            Toast.makeText(context, "Insert failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Insert Success", Toast.LENGTH_SHORT).show();
        }

    }

    //update Collection Name by FirestoreID
    public void updateCollectionNameById(String fireStoreId, String name){
        SQLiteDatabase db = this.getWritableDatabase();

        String sqlUpdate = "update " + "'" + TABLE_NAME + "'"
                + " set " + COLUMN_NAME + " = '" + name + "', "
                +  "where " + FIRESTORE_ID + "= " + fireStoreId;

        db.execSQL(sqlUpdate);

    }

    // returns the firestore id of the item in a table using id
    public HashMap<String, Object> getData(String id, String tableName) {
        // still using rowId to fetch data.. should be changed to either id or firestore_id
        String query = "SELECT * FROM " + "'" + tableName + "'" + " WHERE " + COLUMN_ID + " = " + id;
        HashMap<String, Object> details = new HashMap<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor;
        if (sqLiteDatabase != null) {
            cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                int firestoreId = cursor.getColumnIndex(FIRESTORE_ID);
                int name = cursor.getColumnIndex(COLUMN_NAME);
                details.put("firestore_id", cursor.getString(firestoreId));
                details.put("name", cursor.getString(name));
                return details;
            }
            cursor.close();
        }
        return details;
    }

    /**
     * Delete a collection
     * @param id id for that item in that table
     */
    public void deleteCollection(String id) {
        Log.d(TAG, "deleteData: " + id + " " + TABLE_NAME);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        HashMap<String, Object> collectionDetails = getData(id, TABLE_NAME);
        String firestoreId = (String) collectionDetails.get("firestore_id");
        String collectionName = (String) collectionDetails.get("name");

        // first delete it from COLLECTIONS table
        long status = sqLiteDatabase.delete(TABLE_NAME, FIRESTORE_ID + "=?", new String[]{firestoreId});
        if (status == -1) {
            Toast.makeText(context, "Cannot delete", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Delete success", Toast.LENGTH_SHORT).show();
            // after successfully deleting from COLLECTIONS table, delete the collection's own table
            deleteTable(collectionName);

            // after successful delete in local db, delete in firestore as well
            DocumentReference wishlistRef = fireStore.collection("users").document(userEmail).collection("wishlists").document(firestoreId);
            wishlistRef.delete()
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted! (ID: " + firestoreId + ", user:" + userEmail + ")"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
        }
    }

    public String getDataItem(String rowId, String tableName) {
        // still using rowId to fetch data.. should be changed to either id or firestore_id
        String query = "SELECT * FROM " + "'" + tableName + "'" + " WHERE " + ITEM_ID + " = " + rowId;
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

    // get firestore id of collection using collection name
    public String getCollectionId(String tableName) {
        String query = "SELECT * FROM " + "'" + TABLE_NAME + "'" + " WHERE " + COLUMN_NAME + " = " + "'" + tableName + "'";
        Log.d(TAG, "getCollectionId: " + query);
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

    // deletes an item inside a collection
    public void deleteItem(String itemId, String tableName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Log.d(TAG, "deleteItem: " + itemId + " " + tableName);
        String firestoreItemId = getDataItem(itemId, tableName);
        Log.d(TAG, "deleteItem: " + firestoreItemId);
        String firestoreId = getCollectionId(tableName);
        Log.d(TAG, "deleteItem: " + firestoreItemId + " " + firestoreId);
        tableName = "'" + tableName + "'";
        long status = sqLiteDatabase.delete(tableName, FIRESTORE_ID + "=?", new String[]{firestoreItemId});
        if (status == -1) {
            Toast.makeText(context, "Cannot delete", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Delete success", Toast.LENGTH_SHORT).show();
            // after successful delete in local db, delete in firestore as well
            DocumentReference wishlistRef = fireStore.collection("users").document(userEmail).collection("wishlists").document(firestoreId);
            Map<String, Object> updates = new HashMap<>();
            updates.put(firestoreItemId, FieldValue.delete());
            wishlistRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "DocumentSnapshot " + firestoreItemId + " successfully deleted!");
                    } else {
                        Log.w(TAG, "Error deleting document", task.getException());
                    }
                }
            });
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

    /**
     * Change the collection table name
     * @param oldName String
     * @param newName String
     */
    public void changeTableName(String oldName, String newName) {
        String changeTableName = "ALTER TABLE " + "'" + oldName + "'" + " RENAME TO " + "'" + newName + "'";
        String changeTableNameInCollection = "UPDATE " + TABLE_NAME + " SET "
                + COLUMN_NAME + " = " + "'" + newName + "' WHERE " + COLUMN_NAME + " = " + "'" + oldName + "'";
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL(changeTableNameInCollection);
        sqLiteDatabase.execSQL(changeTableName);
    }


    public void storeImageFirebase(Bitmap bitmap, String name) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        String path = "images/" + userEmail + "/" + name;

        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child(path);
        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            Log.d(TAG, "storeImageFirebase: FAILED (" + path + ") " + exception.getMessage());
            // Handle unsuccessful uploads
        }).addOnSuccessListener(taskSnapshot -> {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            Log.d(TAG, "storeImageFirebase: SUCCESS\nPath: " + taskSnapshot.getMetadata().getPath() + "\nSize (bytes): " + taskSnapshot.getMetadata().getSizeBytes() + "\nContent Type: " + taskSnapshot.getMetadata().getContentType() + "\nCreation Time (ms): " + taskSnapshot.getMetadata().getCreationTimeMillis() + "\nBucket: " + taskSnapshot.getMetadata().getBucket());
            getDownloadUrlFirebase(name);
        });
    }

    public void getDownloadUrlFirebase(String name) {
        String path = "images/" + userEmail + "/" + name;
        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child(path);
        mountainsRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d(TAG, "getDownloadUrlFirebase: " + uri.toString());
            // Got the download URL for 'users/me/profile.png'
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.d(TAG, "getDownloadUrlFirebase: FAILED (" + path + ") " + exception.getMessage());
        });
    }

//    public void downloadFileFirebase(String name) {
//        String path = "images/" + userEmail + "/" + name;
//        StorageReference storageRef = storage.getReference();
//        StorageReference mountainsRef = storageRef.child(path);
//        File localFile = new File(context.getFilesDir(), name);
//    }

    public String getCollectionFireStoreId(String collectionName) {
        String query = "SELECT * FROM COLLECTIONS WHERE COLLECTION_NAME = " + "'" + collectionName + "' AND USER_NAME IS NULL AND FRIEND_ID IS NULL";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        int index = cursor.getColumnIndex(FIRESTORE_ID);
        String id = "";
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                if (cursor.getBlob(4) != null) {
                    id = cursor.getString(4);
                }
            }
        }
        cursor.close();
        return id;
    }
}
