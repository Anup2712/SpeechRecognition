package com.anupam.speechrecogdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.anupam.speechrecogdemo.model.SpeechTextmodel;

public class DBHelper {
    public static final String DATABASE_NAME = "speech.db";
    private static final int DATABASE_VERSION = 1;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;
    public static final String SpeechText = "speech_table";

    private static final String CREATE_SPEECH_TABLE = "CREATE TABLE IF NOT EXISTS " + SpeechText + "( " +
            "id INTEGER PRIMARY KEY, " +
            "speech TEXT, " +
            "frequent INTEGER)";

    public static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_SPEECH_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + SpeechText);
            onCreate(db);
        }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 2);
    }

    public DBHelper(Context ctx) {
        mCtx = ctx;
        mDbHelper = new DatabaseHelper(mCtx);
    }

    public DBHelper open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long InsertSpeechData(SpeechTextmodel registrationClass) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("speech", registrationClass.getSpeechText());
        contentValues.put("frequent", registrationClass.getFrequent());
        long id = mDb.insert(SpeechText, null, contentValues);
        return id;
    }
}
