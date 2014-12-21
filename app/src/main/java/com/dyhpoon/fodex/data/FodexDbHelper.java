package com.dyhpoon.fodex.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dyhpoon.fodex.data.FodexContract.ImageEntry;
import com.dyhpoon.fodex.data.FodexContract.TagEntry;
import com.dyhpoon.fodex.data.FodexContract.ImageTagEntry;


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
                ImageEntry.COLUMN_IMAGE_URI + " TEXT UNIQUE NOT NULL, " +
                ImageEntry.COLUMN_IMAGE_HASH + " TEXT NOT NULL, " +
                ImageEntry.COLUMN_IMAGE_DATE + " TEXT NOT NULL, " +
                "UNIQUE (" + ImageEntry.COLUMN_IMAGE_URI + ") ON CONFLICT IGNORE" +
                " );";

        final String SQL_CREATE_TAG_TABLE = "CREATE TABLE " + TagEntry.TABLE_NAME + " (" +
                TagEntry._ID + " INTEGER PRIMARY KEY, " +
                TagEntry.COLUMN_TAG_NAME + " TEXT UNIQUE NOT NULL, " +
                "UNIQUE (" + TagEntry.COLUMN_TAG_NAME + ") ON CONFLICT IGNORE" +
                " );";

        final String SQL_CREATE_IMAGE_TAG_TABLE = "CREATE TABLE " + ImageTagEntry.TABLE_NAME + " (" +
                ImageTagEntry._ID + " INTEGER PRIMARY KEY, " +
                ImageTagEntry.COLUMN_IT_TAG_ID + " INTEGER NOT NULL, " +
                ImageTagEntry.COLUMN_IT_IMAGE_ID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + ImageTagEntry.COLUMN_IT_TAG_ID + ") REFERENCES " + TagEntry.TABLE_NAME + "(" + TagEntry._ID + ")," +
                "FOREIGN KEY (" + ImageTagEntry.COLUMN_IT_IMAGE_ID + ") REFERENCES " + ImageEntry.TABLE_NAME + "(" + ImageEntry._ID + ")" +
                " );";

        db.execSQL(SQL_CREATE_IMAGE_TABLE);
        db.execSQL(SQL_CREATE_TAG_TABLE);
        db.execSQL(SQL_CREATE_IMAGE_TAG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ImageTagEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ImageEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TagEntry.TABLE_NAME);
    }
}
