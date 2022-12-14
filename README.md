# `GiftMe!`
 Final Project for CS501

## GiftMe! Description:

It is a wishlist app for personal events and holidays (eg. Birthday Parties, Christmas Presents, Secret Santas, Thanksgiving, etc.). It allows users to create a wishlist of items they want to receive for specific occasions. Users can send out an invitation link to their friends and family to let them see the possible gifting options. Friends and family will also be able to see which gifts are already taken by others and which ones are available to prevent duplicate gifts. This saves a lot of time and headache from choosing the right gift for many people since oftentimes it is hard to know what gift is the right gift given the person, the occasion, and who else decided on the same gift. For people who aren’t so close to each other, this is a great way to choose the right gift and build a closer relationship. Another feature we can implement is to add a collaborator feature–eg for wedding wishlists.

## Developers:

Jinpeng Lyu,
Alex Wang,
Lesley Chen,
Tiffany Chen,
Nick (Sangjoon) Lee,
Zizhuang Guo (Tim),

## Error Log:
**Set Up**
* Install App - **PASSED** 

* Sign in - **FAILED** 
  * ERROR message:
```
 Process: com.example.giftme, PID: 19840
    java.lang.ClassCastException: java.lang.Long cannot be cast to java.lang.Integer
        at com.example.giftme.Helpers.DataBaseHelper.convertMapIntoItem(DataBaseHelper.java:277)
        at com.example.giftme.Helpers.DataBaseHelper$1$1.lambda$onComplete$0$com-example-giftme-Helpers-DataBaseHelper$1$1(DataBaseHelper.java:342)
        at com.example.giftme.Helpers.DataBaseHelper$1$1$$ExternalSyntheticLambda0.accept(Unknown Source:6)
        at java.util.HashMap.forEach(HashMap.java:1292)
        at com.example.giftme.Helpers.DataBaseHelper$1$1.onComplete(DataBaseHelper.java:340)
```

* Wishlist Load Collection - **PASSED**

* Wishlist Collection Load Item - - **FAILED** 
  * It is in the Firestore database, but not pulled to local

**Problems of Use:**
* Edit Item Price shows $0.0 so user can't directly type into it.
* 
