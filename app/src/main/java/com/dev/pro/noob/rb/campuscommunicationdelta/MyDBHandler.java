package com.dev.pro.noob.rb.campuscommunicationdelta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class MyDBHandler extends SQLiteOpenHelper {
    Context cont;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "testgcm1.db";
    public static final String TABLE_PRODUCTS = "posts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "post";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        cont = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT " +
                ");";
        db.execSQL(query);
        query = "CREATE TABLE " + "fposts" + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT " +
                ");";
        db.execSQL(query);
        query = "CREATE TABLE " + "dposts" + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    //Add a new row to the database
    public void addName(String p, String tab) {
        String id = "";
        try {
            JSONObject js = new JSONObject(p);
            id = js.getString("msg_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, p);
        values.put(COLUMN_ID, Integer.parseInt(id));
        SQLiteDatabase db = getWritableDatabase();
        db.insert(tab, null, values);
        db.close();
    }

    public String databaseToString() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE 1 ORDER BY " + COLUMN_ID + " DESC;";

        //Cursor points to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();

        //Position after the last row means the end of the results
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_NAME)) != null) {
                dbString += c.getString(c.getColumnIndex(COLUMN_NAME)) + ":";
                dbString += "\n";
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }

    public void Upgrade() {
        SQLiteDatabase db = getWritableDatabase();
        onUpgrade(db, 1, 1);
    }

    public SQLiteDatabase getDB() {
        return getWritableDatabase();
    }
}
