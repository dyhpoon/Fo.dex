package com.dyhpoon.fodex;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.dyhpoon.fodex.data.actual.FodexContract;
import com.dyhpoon.fodex.data.actual.FodexContract.ImageEntry;
import com.dyhpoon.fodex.data.actual.FodexContract.ImageTagEntry;
import com.dyhpoon.fodex.data.actual.FodexContract.ShareEntry;
import com.dyhpoon.fodex.data.actual.FodexContract.TagEntry;
import com.dyhpoon.fodex.data.actual.FodexDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by darrenpoon on 19/12/14.
 */
public class DatabaseTest extends AndroidTestCase {

    public void testCreateDatabase() {
        mContext.deleteDatabase(FodexDbHelper.DATABASE_NAME);
        SQLiteDatabase database = new FodexDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, database.isOpen());
        database.close();
    }

    public void testInsertImageTable() {
        SQLiteDatabase database = new FodexDbHelper(this.mContext).getWritableDatabase();

        ContentValues testImageValues = createImageValues();
        long imageRowId = database.insert(ImageEntry.TABLE_NAME, null, testImageValues);
        assertTrue(imageRowId != -1);
        Cursor imageCursor = database.query(ImageEntry.TABLE_NAME, null, null, null, null, null, null);
        validateCursor(imageCursor, testImageValues);

        ContentValues testTagValues = createTagValues();
        long tagRowId = database.insert(TagEntry.TABLE_NAME, null, testTagValues);
        assertTrue(tagRowId != -1);
        Cursor tagCursor = database.query(TagEntry.TABLE_NAME, null, null, null, null, null, null);
        validateCursor(tagCursor, testTagValues);

        ContentValues testImageTagValues = createImageTagValues(imageRowId, tagRowId);
        long imageTagRowId = database.insert(ImageTagEntry.TABLE_NAME, null, testImageTagValues);
        assertTrue(imageTagRowId != -1);
        Cursor imageTagCursor = database.query(ImageTagEntry.TABLE_NAME, null, null, null, null, null, null);
        validateCursor(imageTagCursor, testImageTagValues);

        ContentValues testShareValues = createShareValues(imageRowId);
        long shareRowId = database.insert(ShareEntry.TABLE_NAME, null, testShareValues);
        assertTrue(shareRowId != -1);
        Cursor shareCursor = database.query(ShareEntry.TABLE_NAME, null, null, null, null, null, null);
        validateCursor(shareCursor, testShareValues);

        database.close();
    }

    static ContentValues createImageValues() {
        ContentValues imageValues = new ContentValues();
        imageValues.put(ImageEntry.COLUMN_IMAGE_ID, "93523");
        imageValues.put(ImageEntry.COLUMN_IMAGE_DATA, "/storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20141115-WA0010.jpg");
        imageValues.put(ImageEntry.COLUMN_IMAGE_DATE_TAKEN, "20141219");
        return imageValues;
    }

    static ContentValues createTagValues() {
        ContentValues tagValues = new ContentValues();
        tagValues.put(TagEntry.COLUMN_TAG_NAME, "morning");
        return tagValues;
    }

    static ContentValues createImageTagValues(long imageRowId, long tagRowId) {
        ContentValues imageTagValues = new ContentValues();
        imageTagValues.put(ImageTagEntry.COLUMN_IT_IMAGE_ID, imageRowId);
        imageTagValues.put(ImageTagEntry.COLUMN_IT_TAG_ID, tagRowId);
        imageTagValues.put(ImageTagEntry.COLUMN_IT_DATE_ADDED, System.currentTimeMillis());
        return imageTagValues;
    }

    static ContentValues createShareValues(long imageRowId) {
        ContentValues shareValues = new ContentValues();
        shareValues.put(FodexContract.ShareEntry.COLUMN_SHARE_IMAGE_ID, imageRowId);
        return shareValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

}
