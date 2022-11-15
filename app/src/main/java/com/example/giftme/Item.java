package com.example.giftme;

public class Item {
    private int id;
    private String name;
    private int hearts;
    private int price;
    private String description;
    private String date;
    private int img; //may change
    private int tableID; //FIRESTORE_ID

    public Item(){

    }
    public Item(int newId, String newName, int newHearts, int newPrice, String newDescription, String newDate, int newImg,  int newTableID){
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
    public void setName( String newName){ name = newName; }
    public void setHearts(int newHearts){ hearts = newHearts;}
    public void setPrice(int newPrice){price = newPrice;}
    public void setDescription( String newDescription){description = newDescription;}
    public void setImg( int newImg){ img = newImg;}
    public void setDate(String newDate){ date = newDate;}
    public void setTableID(int newTableID){ tableID = newTableID;}

    //getters
    public int getId() {return id;}
    public String getName() {return name;}
    public int getHearts() {return hearts;}
    public int getPrice() {return price;}
    public String getDescription() {return description;}
    public String getDate(){return date;}
    public int getImg(){return img;}
    public int getTableID() {return tableID;}

    //toString
    @Override
    public String toString(){
        return id +"; " + name +"; " + hearts +"; " +price + "; " + description + "; " + date +"; " + img + "; " + tableID + "; " ;
    }

}