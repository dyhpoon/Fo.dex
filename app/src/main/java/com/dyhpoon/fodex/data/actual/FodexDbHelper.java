package com.dyhpoon.fodex.data.actual;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dyhpoon.fodex.data.actual.FodexContract.ImageEntry;
import com.dyhpoon.fodex.data.actual.FodexContract.ImageTagEntry;
import com.dyhpoon.fodex.data.actual.FodexContract.ShareEntry;
import com.dyhpoon.fodex.data.actual.FodexContract.TagEntry;


/**
 * Created by darrenpoon on 19/12/14.
 */
public class FodexDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "fodex.db";

    public FodexDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_IMAGE_TABLE = "CREATE TABLE " + ImageEntry.TABLE_NAME + " (" +
                ImageEntry._ID + " INTEGER PRIMARY KEY," +
                ImageEntry.COLUMN_IMAGE_ID + " INTEGER UNIQUE NOT NULL, " +
                ImageEntry.COLUMN_IMAGE_DATA + " TEXT UNIQUE NOT NULL, " +
                ImageEntry.COLUMN_IMAGE_DATE_TAKEN + " INTEGER NOT NULL, " +
                "UNIQUE (" + ImageEntry.COLUMN_IMAGE_DATA + ") ON CONFLICT IGNORE" +
                " );";

        final String SQL_CREATE_TAG_TABLE = "CREATE TABLE " + TagEntry.TABLE_NAME + " (" +
                TagEntry._ID + " INTEGER PRIMARY KEY, " +
                TagEntry.COLUMN_TAG_NAME + " TEXT UNIQUE NOT NULL, " +
                TagEntry.COLUMN_TAG_VISIBLE + " INTEGER NOT NULL DEFAULT 1, " +
                "UNIQUE (" + TagEntry.COLUMN_TAG_NAME + ") ON CONFLICT IGNORE" +
                " );";

        final String SQL_CREATE_IMAGE_TAG_TABLE = "CREATE TABLE " + ImageTagEntry.TABLE_NAME + " (" +
                ImageTagEntry._ID + " INTEGER PRIMARY KEY, " +
                ImageTagEntry.COLUMN_IT_TAG_ID + " INTEGER NOT NULL, " +
                ImageTagEntry.COLUMN_IT_IMAGE_ID + " INTEGER NOT NULL, " +
                ImageTagEntry.COLUMN_IT_DATE_ADDED + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + ImageTagEntry.COLUMN_IT_TAG_ID + ") REFERENCES " + TagEntry.TABLE_NAME + "(" + TagEntry._ID + ") ON DELETE CASCADE," +
                "FOREIGN KEY (" + ImageTagEntry.COLUMN_IT_IMAGE_ID + ") REFERENCES " + ImageEntry.TABLE_NAME + "(" + ImageEntry._ID + ") ON DELETE CASCADE," +
                "UNIQUE (" + ImageTagEntry.COLUMN_IT_TAG_ID + ", " + ImageTagEntry.COLUMN_IT_IMAGE_ID  + ") ON CONFLICT REPLACE" +
                " );";

        final String SQL_CREATE_SHARE_TABLE = "CREATE TABLE " + ShareEntry.TABLE_NAME + " (" +
                ShareEntry._ID + " INTEGER PRIMARY KEY, " +
                ShareEntry.COLUMN_SHARE_IMAGE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + ShareEntry.COLUMN_SHARE_IMAGE_ID + ") REFERENCES " + ImageEntry.TABLE_NAME + "(" + ImageEntry._ID + ") ON DELETE CASCADE, " +
                "UNIQUE (" + ShareEntry.COLUMN_SHARE_IMAGE_ID + ") ON CONFLICT IGNORE" +
                " );";

        db.execSQL(SQL_CREATE_IMAGE_TABLE);
        db.execSQL(SQL_CREATE_TAG_TABLE);
        db.execSQL(SQL_CREATE_IMAGE_TAG_TABLE);
        db.execSQL(SQL_CREATE_SHARE_TABLE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ImageTagEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ImageEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TagEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ShareEntry.TABLE_NAME);
        onCreate(db);
    }
}
