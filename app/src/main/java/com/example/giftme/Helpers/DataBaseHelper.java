package com.example.giftme.Helpers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DataBaseHelper extends SQLiteOpenHelper {
    // global variables
    private final Context context;
    private static String userEmail;
    private static String imgURL;

    // SQLite columns
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
    private static final String COLLECTIONS_USERS = "users";
    private static final String COLLECTIONS_WISHLISTS = "wishlists";

    // debug tag
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

    // initiate database
    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        // fetch user email if the user is logged in
        userEmail = SessionManager.getUserEmail(context);

        // as long as the user is logged in, update the device messaging token for enabling notifications
        if (!userEmail.equals("")) {
            setDeviceMessagingToken(userEmail);
        }
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
                ITEM_HEARTS + " INTEGER, " +
                ITEM_PRICE + " REAL, " +
                ITEM_DESCRIPTION + " TEXT, " +
                ITEM_DATE + " TEXT, " +
                ITEM_IMAGE + " INTEGER, " +
                CLAIMED + " INTEGER, " +
                FIRESTORE_ID + " TEXT " + " ) ";
        database.execSQL(create_table);
    }

    /**
     * some setters and getters
     */
    public void setUserEmail(String email) {
        userEmail = email;
    }

    public void setImgURL(String url) {
        imgURL = url;
    }

    public String getImgURL() {
        return imgURL;
    }

    /**
     * Add a new collection to the database
     *
     * @param email       the email of the user
     * @param displayName the name of the user
     * @param photoUrl    the url of the user's profile picture
     */
    public void createUser(String email, String displayName, String photoUrl) {
        setUserEmail(email);
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("displayName", displayName);
        user.put("photoUrl", photoUrl);
        user.put("friends", new ArrayList<String>());
        Log.d(TAG, "createUser: " + email + " " + displayName + " " + photoUrl);
        fireStore.collection("users").document(email)
                .set(user, SetOptions.merge()).addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: user " + email + " created"))
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: user " + email + " " + e.getMessage()));
    }

    /**
     * callback interface for checkUserExists()
     */
    public interface UserExists {
        void onCallback(boolean exists);
    }

    /**
     * check if the user exists in the database
     *
     * @param email      the email of the user
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
     * set the firebase's device messaging token for the user (needed for notifications)
     * @param email user's email
     */
    public void setDeviceMessagingToken(String email) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (SessionManager.getUserStatus(context)) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "getDeviceMessagingToken: failed to get token");
                }
                String token = task.getResult();
                Log.d(TAG, "getDeviceMessagingToken: " + token);
                Map<String, Object> deviceMessagingToken = new HashMap<>();
                deviceMessagingToken.put("deviceMessagingToken", token);
                DocumentReference docRef = fireStore.collection("users").document(email);
                docRef.set(deviceMessagingToken, SetOptions.merge())
                        .addOnSuccessListener(unused -> Log.d(TAG, "onSuccess: deviceMessagingToken " + token + " created"))
                        .addOnFailureListener(e -> Log.d(TAG, "onFailure: deviceMessagingToken " + token + " " + e.getMessage()));
            }
        });
    }

    /**
     * insert (new) item to [Collection] table
     **/
    public void insertItemIntoCollection(String collection, Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        // insert into SQLite
        String sqlInsert = "insert into " + "'" + collection + "'";
        sqlInsert += " values( null, '" + item.getWebsite()
                + "', '" + item.getName()
                + "', '" + item.getHearts()
                + "', '" + item.getPrice()
                + "', '" + item.getDescription()
                + "', '" + item.getDate()
                + "', '" + item.getImg()
                + "', '" + item.getClaimed()
                + "', '" + item.getFireStoreID() + "' )";
        Log.d(TAG, "insertItemIntoCollection: " + sqlInsert);
        db.execSQL(sqlInsert);

        // parse image path
        String fullImgPath = item.getImg();
        String imgPathFS;
        if (fullImgPath != null) {
            String[] imgPath = fullImgPath.split("/");
            imgPathFS = imgPath[imgPath.length - 1];
        } else {
            imgPathFS = null;
        }
        item.setImg(imgPathFS);

        // generate a map object to put into firestore
        Map<String, Object> firestoreItem = convertItemIntoMap(item);

        String sqlSelect =
                "select " + FIRESTORE_ID + " from "
                        + TABLE_NAME + " where "
                        + COLUMN_NAME + " = '" + collection + "'";
        Cursor cursor = db.rawQuery(sqlSelect, null);
        if (cursor.moveToFirst()) {
            Log.d(TAG, "insertItemIntoCollection: " + cursor.getString(0) + " " + userEmail);
            DocumentReference userDocIdRef = fireStore.collection(COLLECTIONS_USERS).document(userEmail);
            DocumentReference collectionDocIdRef = userDocIdRef.collection("wishlists").document(cursor.getString(0));
            collectionDocIdRef.set(firestoreItem, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
        }
        cursor.close();
    }

    /**
     * send notification to user via firebase
     * @param friendEmail the email of the friend
     * @param InputTitle notification title
     * @param InputBody notification body
     */
    public void sendNotification(String friendEmail, String InputTitle, String InputBody) {
        DocumentReference reference = fireStore.collection(COLLECTIONS_USERS).document(friendEmail);
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot != null) {
                    String token = Objects.requireNonNull(snapshot.getString("deviceMessagingToken")).trim();
                    FCMSend.pushNotification(context, token, InputTitle, InputBody);
                }
            }
        }).addOnFailureListener(e -> System.out.println("Cannot get Token from Firestore."));
    }

    /**
     * insert (new) item to [Collection] table from FireStore (image path needs to be treated differently)
     **/
    public void insertItemIntoCollectionFromFireStore(String collection, Item item) {
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
                + "', '" + item.getFireStoreID() + "' )";
        Log.d(TAG, "insertItemIntoCollection: " + sqlInsert);
        db.execSQL(sqlInsert);

        // generate a map object to put into firestore
        Map<String, Object> firestoreItem = convertItemIntoMap(item);

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

    /**
     * get the firestore id of a collection
     **/
    public String getCollectionFirestoreId(String collectionName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sqlSelect =
                "select " + FIRESTORE_ID + " from "
                        + TABLE_NAME + " where "
                        + COLUMN_NAME + " = '" + collectionName + "'";
        Cursor cursor = db.rawQuery(sqlSelect, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        } else {
            return null;
        }
    }

    /**
     * convert item object into a hashmap (for firestore)
     **/
    public Map<String, Object> convertItemIntoMap(Item item) {
        Map<String, Object> itemMap = new HashMap<>();
        Map<String, Object> nestedItemMap = new HashMap<>();
        itemMap.put("url", item.getWebsite());
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

    /**
     * convert a hashmap into an item object (for SQLite)
     **/
    public Item convertMapIntoItem(Map<String, Object> map, String itemID) {
        Item item = new Item();
        item.setName(String.valueOf(map.get("name")));
        if (map.get("hearts") != null) {
            item.setHearts(Math.toIntExact(((Long) map.get("hearts"))));
        } else {
            item.setHearts(0);
        }
        if (map.get("price") != null) {
            final Object object = map.get("price");
            final double d = ((Number) object).doubleValue();
            item.setPrice(d);
        } else {
            item.setPrice(0);
        }
        item.setDescription((String) map.get("description"));
        item.setDate((String) map.get("date"));
        item.setImg((String) map.get("img"));
        item.setClaimed((Boolean) map.get("claimed"));
        item.setWebsite((String) map.get("url"));
        item.setKnownFireStoreID(itemID);
        return item;
    }

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
     * get all collections from User on firestore and add to the local database
     */
    public void getCollectionsFromUser(String userID) {
        DocumentReference userRef = fireStore.collection(COLLECTIONS_USERS).document(userID);
        userRef.collection(COLLECTIONS_WISHLISTS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            String collectionName = (String) document.getData().get("Collection Name");
                            String friendID = (String) document.getData().get("Friend ID");
                            String collectionID = document.getId();
                            //if this is user's own wishlist
                            if (friendID == null || friendID.equalsIgnoreCase("null")) {
                                addOldCollectionSQL(null, collectionName, null, collectionID, null);
                                createNewTable(collectionName);
                                //add items into the collection
                                //get fire store collection wishlist items
                                DocumentReference collectionRef = userRef.collection("wishlists").document(collectionID);
                                collectionRef.get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot doc = task1.getResult();
                                        if (doc.exists()) {
                                            Map<String, Object> itemsInWishlist = doc.getData();
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                itemsInWishlist.forEach((key, value) -> {
                                                            if (value instanceof HashMap) {
                                                                Item currentItem = convertMapIntoItem((Map<String, Object>) value, key);
                                                                Log.d("ITEM", currentItem.toString());
                                                                insertItemIntoCollectionFromFireStore(collectionName, currentItem);
                                                            }
                                                        }
                                                );
                                            }
                                        }
                                    } else {
                                        Log.d("ToastError", "error");
                                    }
                                });
                            } else {
                                //this is user's friend's collections
                                DocumentReference userRefFriend = fireStore.collection("users").document(friendID);
                                String displayName = "displayName";
                                String photoURL = "photoUrl";
                                final String[] friend = new String[2];
                                //friend[0] = name; friend[1] = pfp
                                userRefFriend.get().addOnCompleteListener(task12 -> {
                                    if (task12.isSuccessful()) {
                                        DocumentSnapshot user = task12.getResult();
                                        friend[0] = user.getString(displayName);
                                        friend[1] = user.getString(photoURL);
                                        //add collections
                                        addOldCollectionSQL(friend[0], collectionName, friendID, collectionID, friend[1]);
                                    }
                                });
                            }
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    /**
     * get all items from a collection table
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
     * update item in database
     */
    public void updateById(String collection_name, String url, int id, String name, double price, String description,
                           int hearts, String img, String fireStoreId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG, "updateById: " + collection_name + " " + id + " " + name + " " + price + " " + description + " " + hearts + " " + img + " " + fireStoreId);

        // update SQLite
        String sqlUpdate = "update " + "'" + collection_name + "'"
                + " set " + ITEM_NAME + " = '" + name + "', "
                + ITEM_HEARTS + "= '" + hearts + "', "
                + ITEM_URL + "= '" + url + "', "
                + ITEM_PRICE + "= '" + price + "', "
                + ITEM_DESCRIPTION + "= '" + description + "', "
                + ITEM_IMAGE + "= '" + img + "' "
                + "where " + FIRESTORE_ID + "= " + "'" + fireStoreId + "'";
        Log.d(TAG, "updateById: " + sqlUpdate);

        db.execSQL(sqlUpdate);

        String sqlSelect =
                "select " + FIRESTORE_ID + " from "
                        + TABLE_NAME + " where "
                        + COLUMN_NAME + " = '" + collection_name + "'";
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sqlSelect, null);

        // parse image path
        String imgPathFS;
        if (img != null) {
            String[] imgPath = img.split("/");
            imgPathFS = imgPath[imgPath.length - 1];
        } else {
            imgPathFS = null;
        }

        // update Firestore
        if (cursor.moveToFirst()) {
            DocumentReference userDocIdRef = fireStore.collection(COLLECTIONS_USERS).document(userEmail);
            DocumentReference collectionDocIdRef = userDocIdRef.collection(COLLECTIONS_WISHLISTS).document(cursor.getString(0));
            String urlField = fireStoreId + ".url";
            String nameField = fireStoreId + ".name";
            String priceField = fireStoreId + ".price";
            String descriptionField = fireStoreId + ".description";
            String heartsField = fireStoreId + ".hearts";
            String imgField = fireStoreId + ".img";
            collectionDocIdRef.update(urlField, url, nameField, name, priceField, price, descriptionField, description, heartsField, hearts, imgField, imgPathFS).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully updated! " + cursor.getString(0) + " " + fireStoreId);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error updating document", e);
                }
            });
        }
    }


    /**
     * Read all the data from a specific table
     *
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
     * add a friend inside the user's field in firestore
     * @param friendId
     */
    public void addFriend(String friendId) {
        Log.d(TAG, "addFriend: " + userEmail + " " + friendId);
        DocumentReference userDocIdRef = fireStore.collection("users").document(userEmail);
        userDocIdRef.update("friends", FieldValue.arrayUnion(friendId));
    }

    /**
     * get list of friends from the current user from firestore
     */
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
     *
     * @param userName etc
     */
    public void addNewCollection(String userName, String collectionName, String friendID) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // add to firestore first to make sure the collection is created
        Map<String, Object> wishlist = new HashMap<>();
        wishlist.put("Collection Name", collectionName);
        wishlist.put("Friend ID", friendID);
        DocumentReference userDocIdRef = fireStore.collection("users").document(userEmail);
        DocumentReference wishlistDocIdRef = userDocIdRef.collection("wishlists").document();
        wishlistDocIdRef.set(wishlist)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written! (ID: " + wishlistDocIdRef.getId() + ", user:" + userEmail + ")"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

        // insert into SQLite
        values.put(COLUMN_NAME, collectionName);
        values.put(USER_NAME, userName);
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
     *
     * @param friendName     friend's name
     * @param collectionName name of the collection
     * @param friendID       friend's email
     * @param fsID           firestoreId
     */
    public void addNewFriendCollection(String friendName, String collectionName, String friendID, String fsID, String pfp) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // add to firestore first to make sure the collection is created
        Map<String, Object> wishlist = new HashMap<>();
        wishlist.put("Collection Name", collectionName);
        wishlist.put("Friend ID", friendID);

        DocumentReference userDocIdRef = fireStore.collection("users").document(userEmail);
        DocumentReference wishlistDocIdRef = userDocIdRef.collection("wishlists").document(fsID);
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

    /**
     * for adding friend's collections
     *
     * @param friendName     friend's name
     * @param collectionName name of the collection
     * @param friendID       friend's email
     * @param fsID           firestoreId
     */
    public void addOldCollectionSQL(String friendName, String collectionName, String friendID, String fsID, String pfp) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

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
    public void updateCollectionNameById(String fireStoreId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        String sqlUpdate = "update " + "'" + TABLE_NAME + "'"
                + " set " + COLUMN_NAME + " = '" + name + "', "
                + "where " + FIRESTORE_ID + "= " + fireStoreId;

        db.execSQL(sqlUpdate);

    }

    // returns the firestore id of the item in a table using id
    public HashMap<String, Object> getData(String id, String tableName) {
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
     *
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

    /**
     * Delete friend's collection
     *
     * @param id id for that item in that table
     */
    public void deleteCollectionFriend(String id) {
        Log.d(TAG, "deleteData: " + id + " " + TABLE_NAME);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // first delete it from COLLECTIONS table
        long status = sqLiteDatabase.delete(TABLE_NAME, FIRESTORE_ID + "=?", new String[]{id});
        if (status == -1) {
            Toast.makeText(context, "Cannot delete", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Delete success", Toast.LENGTH_SHORT).show();
            // after successful delete in local db, delete in firestore as well
            DocumentReference wishlistRef = fireStore.collection("users").document(userEmail).collection("wishlists").document(id);
            wishlistRef.delete()
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted! (ID: " + id + ", user:" + userEmail + ")"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
        }
    }

    /**
     * Delete a collection SQLite only
     *
     * @param id id for that item in that table
     */
    public void deleteCollectionSQL(String id) {
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

        }
    }

    /**
     * fetch item from SQLite using item id
     */
    public String getDataItem(String rowId, String tableName) {
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
     *
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
     *
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
        fireStore.collection(COLLECTIONS_USERS).document(SessionManager.getUserEmail(context))
                .collection(COLLECTIONS_WISHLISTS).document(getCollectionId(newName))
                .update("Collection Name", newName);
    }

    /**
     * store image in firebase storage
     * @param bitmap
     * @param name
     */
    public void storeImageFirebase(Bitmap bitmap, String name) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        // the path goes like this: "images/<user's email>/<name of the image (in jpg)>"
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
            getDownloadUrlFirebase(userEmail, name);
        });
    }

    /**
     * remove image from firebase storage
     */
    public void removeImageFirebase(String name) {
        String path = "images/" + userEmail + "/" + name;

        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child(path);
        mountainsRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "removeImageFirebase: SUCCESS " + path);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "removeImageFirebase: FAILED " + path);
            }
        });
    }

    /**
     * get download url of the image from firebase storage
     * @param email
     * @param name
     */
    public void getDownloadUrlFirebase(String email, String name) {
        String path = "images/" + email + "/" + name;
        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child(path);
        mountainsRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d(TAG, "getDownloadUrlFirebase: " + uri.toString());
            setImgURL(uri.toString());
            // Got the download URL for 'users/me/profile.png'
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.d(TAG, "getDownloadUrlFirebase: FAILED (" + path + ") " + exception.getMessage());
        });
    }

    /**
     * get the firestore id from the collection name
     * @param collectionName
     * @return firestore id of the collection
     */
    public String getCollectionFireStoreId(String collectionName) {
        String query = "SELECT * FROM COLLECTIONS WHERE COLLECTION_NAME = " + "'" + collectionName + "' AND USER_NAME IS NULL AND FRIEND_ID IS NULL";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        String id = "";
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                if (cursor.getBlob(5) != null) {
                    id = cursor.getString(5);
                }
            }
        }
        cursor.close();
        return id;
    }
}
