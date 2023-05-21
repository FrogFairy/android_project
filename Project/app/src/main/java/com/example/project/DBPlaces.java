package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBPlaces {

    private static final String DATABASE_NAME = "simple.db";
    private static final int DATABASE_VERSION = 2;
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

    private SQLiteDatabase mDataBase;

    public DBPlaces(Context context) {
        OpenHelper mOpenHelper = new OpenHelper(context);
        mDataBase = mOpenHelper.getWritableDatabase();
    }

    public long insert(String table_name, String[] values) {
        if (table_name.equals(PLACES_TABLE_NAME)) {
            return insertPlaces(values[0], Float.valueOf(values[1]), Float.valueOf(values[2]),
                    values[3], values[4]);
        } else if (table_name.equals(USERS_TABLE_NAME)) {
            return insertUsers(values[0]);
        }
        return insertVisiting(Long.getLong(values[0]), Long.getLong(values[1]), values[2]);
    }

    public long insertPlaces(String address, float lat, float lon, String description, String image) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ADDRESS, address);
        cv.put(COLUMN_LATITUDE, lat);
        cv.put(COLUMN_LONGITUDE, lon);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_IMAGE, image);
        return mDataBase.insert(PLACES_TABLE_NAME, null, cv);
    }

    public long insertUsers(String username) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERNAME, username);
        return mDataBase.insert(USERS_TABLE_NAME, null, cv);
    }

    public long insertVisiting(long user_id, long place_id, String images) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER_ID, user_id);
        cv.put(COLUMN_PLACE_ID, place_id);
        cv.put(COLUMN_IMAGES, images);
        return mDataBase.insert(VISITING_TABLE_NAME, null, cv);
    }

    public int update(Places md) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ADDRESS, md.getAddress());
        cv.put(COLUMN_LATITUDE, md.getLatitude());
        cv.put(COLUMN_LONGITUDE, md.getLongitude());
        cv.put(COLUMN_DESCRIPTION, md.getDescription());
        cv.put(COLUMN_IMAGE, md.getImage());
        return mDataBase.update(PLACES_TABLE_NAME, cv, COLUMN_ID + " = ?", new String[] {String.valueOf(md.getId())});
    }

    public int update(Users md) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERNAME, md.getUsername());
        return mDataBase.update(USERS_TABLE_NAME, cv, COLUMN_ID + " = ?", new String[] {String.valueOf(md.getId())});
    }

    public int update(Visiting md) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER_ID, md.getUserId());
        cv.put(COLUMN_PLACE_ID, md.getPlaceId());
        cv.put(COLUMN_IMAGES, md.getImages());
        return mDataBase.update(VISITING_TABLE_NAME, cv, COLUMN_ID + " = ?", new String[] {String.valueOf(md.getId())});
    }

    public void deleteAll(String table_name) {
        mDataBase.delete(table_name, null, null);
    }

    public void delete(String table_name, long id) {
        mDataBase.delete(table_name, COLUMN_ID + " = ?", new String[] {String.valueOf(id) });
    }

    public Object select(String table_name, long id) {
        if (table_name.equals(PLACES_TABLE_NAME)) return selectPlaces(id);
        else if (table_name.equals(USERS_TABLE_NAME)) return selectUsers(id);
        return selectVisiting(id);
    }

    public Places selectPlaces(long id) {
        Cursor mCursor = mDataBase.query(PLACES_TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        mCursor.moveToFirst();
        String Address = mCursor.getString(NUM_COLUMN_ADDRESS);
        float Latitude = mCursor.getFloat(NUM_COLUMN_LATITUDE);
        float Longitude = mCursor.getFloat(NUM_COLUMN_LONGITUDE);
        String Description = mCursor.getString(NUM_COLUMN_DESCRIPTION);
        String Image = mCursor.getString(NUM_COLUMN_IMAGE);
        return new Places(id, Address, Latitude,   Longitude, Description, Image);
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

    private class OpenHelper extends SQLiteOpenHelper {

        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "CREATE TABLE " + PLACES_TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ADDRESS + " TEXT, " +
                    COLUMN_LATITUDE + " FLOAT, " +
                    COLUMN_LONGITUDE + " FLOAT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_IMAGE + " TEXT);";
            db.execSQL(query);
            query = "CREATE TABLE " + USERS_TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT);";
            db.execSQL(query);
            query = "CREATE TABLE " + VISITING_TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USER_ID + " INTEGER, " +
                    COLUMN_PLACE_ID + " INTEGER, " +
                    COLUMN_IMAGES + " TEXT);";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + PLACES_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + VISITING_TABLE_NAME);
            onCreate(db);
        }
    }

}
