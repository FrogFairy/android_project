package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DBPlaces {

    private static final String DATABASE_NAME = "simple.db";
    private static final String PLACES_TABLE_NAME = "tablePlaces";
    private static final String USERS_TABLE_NAME = "tableUsers";
    private static final String VISITING_TABLE_NAME = "tableVisiting";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE = "image";

    private static final String COLUMN_USERNAME = "username";

    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_PLACE_ID = "place_id";
    private static final String COLUMN_IMAGES = "images";

    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_ADDRESS = 1;
    private static final int NUM_COLUMN_LATITUDE = 2;
    private static final int NUM_COLUMN_LONGITUDE = 3;
    private static final int NUM_COLUMN_DESCRIPTION = 4;
    private static final int NUM_COLUMN_IMAGE = 5;

    private static final int NUM_COLUMN_USERNAME = 1;

    private static final int NUM_COLUMN_USER_ID = 1;
    private static final int NUM_COLUMN_PLACE_ID = 2;
    private static final int NUM_COLUMN_IMAGES = 3;

    private FirebaseFirestore mDataBase;
    private CollectionReference placesDoc;
    private CollectionReference usersDoc;
    private CollectionReference visitingDoc;

    public DBPlaces() {
        mDataBase = FirebaseFirestore.getInstance();
        placesDoc = mDataBase.collection(PLACES_TABLE_NAME);
        usersDoc = mDataBase.collection(USERS_TABLE_NAME);
        visitingDoc = mDataBase.collection(VISITING_TABLE_NAME);
    }

    public void insertPlaces(String address, float lat, float lon, String description, String image) {
        Map<String, Object> place = new HashMap<>();
        place.put("address", address);
        place.put("latitude", lat);
        place.put("longitude", lon);
        place.put("description", description);
        place.put("image", image);
        placesDoc.document(address).set(place);
    }

    public void insertUsers(String username) {
        Map<String, Object> place = new HashMap<>();
        place.put("username", username);
        usersDoc.document(username).set(place);
    }

    public void insertVisiting(String user_id, String place_id, String images) {
        Map<String, Object> place = new HashMap<>();
        place.put("user_id", user_id);
        place.put("place_id", place_id);
        place.put("images", images);
        visitingDoc.document(user_id + " " + place_id).set(place);
    }

    public String selectPlaces(String id) {
        DocumentReference place = mDataBase.collection(PLACES_TABLE_NAME).document(id);
        place.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    return task.getResult();
                }
                return null;
            }
        });
    }

    public Places selectPlaces(String address) {
        Cursor mCursor = mDataBase.query(PLACES_TABLE_NAME, null, COLUMN_ADDRESS + " = ?", new String[]{address}, null, null, null);

        mCursor.moveToFirst();
        int id = mCursor.getInt(NUM_COLUMN_ID);
        float Latitude = mCursor.getFloat(NUM_COLUMN_LATITUDE);
        float Longitude = mCursor.getFloat(NUM_COLUMN_LONGITUDE);
        String Description = mCursor.getString(NUM_COLUMN_DESCRIPTION);
        String Image = mCursor.getString(NUM_COLUMN_IMAGE);
        return new Places(id, address, Latitude, Longitude, Description, Image);
    }

    public Users selectUsers(long id) {
        Cursor mCursor = mDataBase.query(USERS_TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        mCursor.moveToFirst();
        String Username = mCursor.getString(NUM_COLUMN_USERNAME);
        return new Users(id, Username);
    }

    public Users selectUsers(String username) {
        Cursor mCursor = mDataBase.query(USERS_TABLE_NAME, null, COLUMN_USERNAME + " = ?", new String[]{username}, null, null, null);

        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            int id = mCursor.getInt(NUM_COLUMN_ID);
            return new Users(id, username);
        }
        return null;
    }

    public Visiting selectVisiting(long id) {
        Cursor mCursor = mDataBase.query(VISITING_TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        mCursor.moveToFirst();
        long UserId = mCursor.getLong(NUM_COLUMN_USER_ID);
        long PlaceId = mCursor.getLong(NUM_COLUMN_PLACE_ID);
        String Images = mCursor.getString(NUM_COLUMN_IMAGES);
        return new Visiting(id, UserId, PlaceId, Images);
    }

    public Visiting selectVisiting(Users user) {
        Cursor mCursor = mDataBase.query(VISITING_TABLE_NAME, null, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(user.getId())}, null, null, null);

        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            long Id = mCursor.getLong(NUM_COLUMN_ID);
            long PlaceId = mCursor.getLong(NUM_COLUMN_PLACE_ID);
            String Images = mCursor.getString(NUM_COLUMN_IMAGES);
            return new Visiting(Id, user.getId(), PlaceId, Images);
        }
        return null;
    }

    public Object selectAll(String table_name) {
        if (table_name.equals(PLACES_TABLE_NAME)) {
            return selectAllPlaces();
        } else if (table_name.equals(USERS_TABLE_NAME)) {
            return selectAllUsers();
        }
        return selectAllVisiting();
    }

    public ArrayList<Places> selectAllPlaces() {
        Cursor mCursor = mDataBase.query(PLACES_TABLE_NAME, null, null, null, null, null, null);

        ArrayList<Places> arr = new ArrayList<Places>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                String Address = mCursor.getString(NUM_COLUMN_ADDRESS);
                float Latitude = mCursor.getFloat(NUM_COLUMN_LATITUDE);
                float Longitude = mCursor.getFloat(NUM_COLUMN_LONGITUDE);
                String Description = mCursor.getString(NUM_COLUMN_DESCRIPTION);
                String Image = mCursor.getString(NUM_COLUMN_IMAGE);
                arr.add(new Places(id, Address, Latitude, Longitude, Description, Image));
            } while (mCursor.moveToNext());
        }
        return arr;
    }

    public ArrayList<Users> selectAllUsers() {
        Cursor mCursor = mDataBase.query(USERS_TABLE_NAME, null, null, null, null, null, null);

        ArrayList<Users> arr = new ArrayList<Users>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                String Username = mCursor.getString(NUM_COLUMN_USERNAME);
                arr.add(new Users(id, Username));
            } while (mCursor.moveToNext());
        }
        return arr;
    }

    public ArrayList<Visiting> selectAllVisiting() {
        Cursor mCursor = mDataBase.query(PLACES_TABLE_NAME, null, null, null, null, null, null);

        ArrayList<Visiting> arr = new ArrayList<Visiting>();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                long id = mCursor.getLong(NUM_COLUMN_ID);
                long User_id = mCursor.getLong(NUM_COLUMN_USER_ID);
                long Place_id = mCursor.getLong(NUM_COLUMN_PLACE_ID);
                String Images = mCursor.getString(NUM_COLUMN_IMAGES);
                arr.add(new Visiting(id, User_id, Place_id, Images));
            } while (mCursor.moveToNext());
        }
        return arr;
    }
}
