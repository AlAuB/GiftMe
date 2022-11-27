package com.example.giftme;

import android.util.Log;

import java.util.UUID;

public class Item {
    //can look into implementing parcelable

    private int id;
    private String name;
    private int hearts;
    private int price;
    private String description;
    private String date;
    private int img; //may change
    private String tableID; //FIRESTORE_ID

    public Item(){
        // there should be a way to tell if the item object is being created just for the UI
        // or if it is being created for the database
        setTableID();
    }
    public Item(int newId, String newName, int newHearts, int newPrice, String newDescription, String newDate, int newImg){
        setId(newId);
        setName(newName);
        setHearts(newHearts);
        setPrice(newPrice);
        setDescription(newDescription);
        setDate(newDate);
        setImg(newImg);
        setTableID();
    }

    //setters
    public void setId(int newId){ id = newId; }
    public void setName( String newName){ name = newName; }
    public void setHearts(int newHearts){ hearts = newHearts;}
    public void setPrice(int newPrice){price = newPrice;}
    public void setDescription( String newDescription){description = newDescription;}
    public void setImg( int newImg){ img = newImg;}
    public void setDate(String newDate){ date = newDate;}
    // using Java's UUID class to generate a unique ID for each item (cryptographically strong pseudo random number generator)
    public void setTableID(){ tableID = UUID.randomUUID().toString();}

    //getters
    public int getId() {return id;}
    public String getName() {return name;}
    public int getHearts() {return hearts;}
    public int getPrice() {return price;}
    public String getDescription() {return description;}
    public String getDate(){return date;}
    public int getImg(){return img;}
    public String getTableID() {return tableID;}

    //toString
    @Override
    public String toString(){
        return id +"; " + name +"; " + hearts +"; " +price + "; " + description + "; " + date +"; " + img + "; " + tableID + "; " ;
    }

}