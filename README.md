# `GiftMe!`
Android App built for CS501 E1 FALL 2022 at Boston University

## 🎁 GiftMe! Description:

GiftMe! is a wishlist app for personal events and holidays (eg. Birthday Parties, Christmas Presents, Secret Santas, Thanksgiving, etc.). It allows users to create a wishlist of items they want to receive for specific occasions. Users can send out an invitation link to their friends and family to let them see the possible gifting options. Friends and family will also be able to see which gifts are already taken by others and which ones are available to prevent duplicate gifts. This saves a lot of time and headache from choosing the right gift for many people since oftentimes it is hard to know what gift is the right gift given the person, the occasion, and who else decided on the same gift.

## 🎀 Features

**Guest Users**

Users do not need to sign in to view and claim items from friends' wishlists. However, friends' wishlists will not be saved to an account. Thus, when you do log in, the wishlists you viewed as a Guest User will not be saved and must be added again. As a guest user, you cannot create a wishlist. 

**Creating a Wishlist**
- Log in with Google Account
- Create wishlist
- Add items to wishlist
- Edit or delete items if you want
- Share the wishlink with your friend!

**Receiving Wishlist Link From Friend**
- Click on the link or copy the link
- Your friend’s wishlist will be automatically added or you can manually add the copied link
- Click on your friend’s wishlist to see their items
- Click on the item to see more details
- Claim the item you want to get for your friend

**Claim Items**
- Once you have decided to claim an item of a friend, you can mark the wish as "claimed" and others viewing the wishlist will be able to see that it has been claimed.

**Information you can specify for the items you want**
- Title
- Description
- Price
- Link
- Image
- Preference Level

**Compact and Detailed Views**

We have added two views to view items in a wishlist. The user is able to toggle between these two as preferred. 

Compact/Simple View:
- For those who prefer a simpler, quicker way of adding items and do not care about the details of the item

Detailed View:
- For those who prefer adding details to items and want to view the items in detail.

## 🎀 Technologies ##

**Languages used:** 
- Java, XML, SQLite

**APIs:**
- [FireStore Database](https://firebase.google.com/docs/firestore) as data storage
- [Firebase Dynamic Links](https://firebase.google.com/docs/dynamic-links) to generate links to firestore collections
- [Google Authentication](https://developers.google.com/identity/sign-in/android/start-integrating) to log in

**Software:**
- [Android Studio](https://developer.android.com/) 
- Compile SDK Version: 33
- Minimum SDK Version: 22
- Use of RecyclerView and CardViews, Fragments, Menu, Gestures for delete, edit, and back. 

## 🎀 Installation ##
- Download code to run in Android Studio emulator
- We will need to add your SHA-1 verification code for your device to access firestore database since this project is still in development

## 🎀 Developers:

Jinpeng Lyu [@AlAuB](https://github.com/AlAuB), 
Alex Wang [@AlxWang9966](https://github.com/AlxWang9966),
Lesley Chen [@lchen456](https://github.com/lchen456), 
Tiffany Chen [@txcchen](https://github.com/txcchen),
Nick (Sangjoon) Lee [@sj0726](https://github.com/sj0726),
Zizhuang (Tim) Guo [@Guo-Zizhuang](https://github.com/Guo-Zizhuang),
