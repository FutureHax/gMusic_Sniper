package com.t3hh4xx0r.gmusicsniper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
	
    public static final String KEY_ROWID = "Id";
    public static final String KEY_TITLE = "Title";
    public static final String KEY_ARTIST = "Artist";
    public static final String KEY_ALBUM = "Album";

    private static final String DATABASE_NAME = Constants.gMusicSniperDir + Constants.musicDB;
    private static final String DATABASE_TABLE = "MUSIC";
    
    private static final int DATABASE_VERSION = 37;

    
    private final Context context; 
    
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

		@Override
		public void onCreate(SQLiteDatabase arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
    }
    
    //---opens the database---
    public DBAdapter open() throws SQLException {
        db = SQLiteDatabase.openDatabase(DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY);
        return this;
    }

    //---closes the database---    
    public void close() {
        DBHelper.close();
    }
    
    //---retrieves a particular title---
    public Cursor getTitle(int songFinalValue) throws SQLException {
        Cursor mCursor =
        		db.query(DATABASE_TABLE, new String [] {KEY_TITLE, KEY_ARTIST, KEY_ALBUM}, KEY_ROWID + " = \'" + songFinalValue + "\'", null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

}