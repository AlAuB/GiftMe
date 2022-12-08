package com.example.giftme.Helpers;

import androidx.annotation.NonNull;

import android.util.Log;

import java.util.UUID;

public class Item {
    //can look into implementing parcelable

    private int id;
    private String name;
    private float hearts;
    private int price;
    private String website;
    private String description;
    private String date;
    private String img;
    private String fireStore_ID;
    private boolean claimed;

    public Item(){
        // there should be a way to tell if the item object is being created just for the UI
        // or if it is being created for the database
        //setClaimed();
        setFireStoreID();
    }
    public Item(int newId, String website, String newName, float newHearts, int newPrice, String newDescription, String newDate,
                String newImg){
        setWebsite(website);
        setId( newId);
        setName(newName);
        setHearts(newHearts);
        setPrice(newPrice);
        setDescription(newDescription);
        setDate(newDate);
        setImg(newImg);
        //setClaimed(isClaimed);
        setFireStoreID();
    }

    //setters
    public void setId(int newId){ id = newId; }
    public void setWebsite(String URL) {website = URL;}
    public void setName( String newName){ name = newName; }
    public void setHearts(float newHearts){ hearts = newHearts;}
    public void setPrice(int newPrice){price = newPrice;}
    public void setDescription( String newDescription){description = newDescription;}
    public void setImg( String newImg){ img = newImg;}
    public void setDate(String newDate){ date = newDate;}
    public void setClaimed(Boolean isClaimed){
        claimed = isClaimed;
        //isGood
        //need to update item
        }
    // using Java's UUID class to generate a unique ID for each item (cryptographically strong pseudo random number generator)
    public void setFireStoreID(){ fireStore_ID = UUID.randomUUID().toString();}

    public void setKnownFireStoreID(String newItemID){ fireStore_ID = newItemID;}
    //getters
    public int getId() {return id;}
    public String getWebsite() {return website;}
    public String getName() {return name;}
    public float getHearts() {return hearts;}
    public int getPrice() {return price;}
    public String getDescription() {return description;}
    public String getDate(){return date;}
    public String getImg(){return img;}
    public boolean getClaimed(){return claimed;}
    public String getFireStoreID() {return fireStore_ID;}

    //toString
    @NonNull
    @Override
    public String toString(){
        return id +"; " + name +"; " + hearts +"; " +price + "; " + description + "; " + date +"; " + img + "; " + fireStore_ID + "; " ;
    }
}