package com.example.giftme;

import androidx.annotation.NonNull;

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
    private String tableID; //FIRESTORE_ID

    public Item(){

    }
    public Item(int newId, String website, String newName, float newHearts, int newPrice, String newDescription, String newDate, String newImg,  String newTableID){
        setWebsite(website);
        setId( newId);
        setName(newName);
        setHearts(newHearts);
        setPrice(newPrice);
        setDescription(newDescription);
        setDate(newDate);
        setImg(newImg);
        setTableID(newTableID);
    }

    //setters
    public void setId( int newId){ id = newId; }
    public void setWebsite(String URL) {website = URL;}
    public void setName( String newName){ name = newName; }
    public void setHearts(float newHearts){ hearts = newHearts;}
    public void setPrice(int newPrice){price = newPrice;}
    public void setDescription( String newDescription){description = newDescription;}
    public void setImg( String newImg){ img = newImg;}
    public void setDate(String newDate){ date = newDate;}
    public void setTableID(String newTableID){ tableID = newTableID;}

    //getters
    public int getId() {return id;}
    public String getWebsite() {return website;}
    public String getName() {return name;}
    public float getHearts() {return hearts;}
    public int getPrice() {return price;}
    public String getDescription() {return description;}
    public String getDate(){return date;}
    public String getImg(){return img;}
    public String getTableID() {return tableID;}

    //toString
    @NonNull
    @Override
    public String toString(){
        return id +"; " + name +"; " + hearts +"; " +price + "; " + description + "; " + date +"; " + img + "; " + tableID + "; " ;
    }
}