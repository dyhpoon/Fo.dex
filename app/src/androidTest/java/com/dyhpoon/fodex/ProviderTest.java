package com.dyhpoon.fodex;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.dyhpoon.fodex.data.FodexContract.ImageEntry;
import com.dyhpoon.fodex.data.FodexContract.ImageTagEntry;
import com.dyhpoon.fodex.data.FodexContract.TagEntry;
import com.dyhpoon.fodex.data.FodexDbHelper;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Created by darrenpoon on 19/12/14.
 */
public class ProviderTest extends AndroidTestCase {

    private long mImageRowId = -1;
    private long mTagRowId = -1;
    private long mImageTagRowId = -1;
    private SQLiteDatabase mDatabase;
    private ContentValues mImageValues = createImageValues();
    private ContentValues mTagValues = createTagValues();
    private ContentValues mImageTagValues;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext.deleteDatabase(FodexDbHelper.DATABASE_NAME);

        mDatabase = new FodexDbHelper(this.mContext).getWritableDatabase();

        mImageRowId = mDatabase.insert(ImageEntry.TABLE_NAME, null, mImageValues);
        assertTrue(mImageRowId != -1);

        mTagRowId = mDatabase.insert(TagEntry.TABLE_NAME, null, mTagValues);
        assertTrue(mTagRowId != -1);

        mImageTagValues = createImageTagValues(mImageRowId, mTagRowId);
        assertNotNull(mImageTagValues);

        mImageTagRowId = mDatabase.insert(ImageTagEntry.TABLE_NAME, null, mImageTagValues);
        assertTrue(mImageRowId != -1);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mDatabase.close();
    }

    public void testImageIdUri() {
        Cursor imageCursor = mContext.getContentResolver().query(
                ImageEntry.buildImageUri(mImageRowId),
                null,
                null,
                null,
                null);
        assertEquals(imageCursor.getCount(), 1);
        validateCursor(imageCursor, mImageValues);
        imageCursor.close();
    }

    public void testImageHashUri() {
        Cursor imageHashCursor = mContext.getContentResolver().query(
                ImageEntry.buildHash(mImageValues.getAsString(ImageEntry.COLUMN_IMAGE_HASH)),
                null,
                null,
                null,
                null);
        assertEquals(imageHashCursor.getCount(), 1);
        validateCursor(imageHashCursor, mImageValues);
        imageHashCursor.close();
    }

    public void testImageHashDateUri() {
        Cursor imageHashDateCursor = mContext.getContentResolver().query(
                ImageEntry.buildHashWithDate(
                        mImageValues.getAsString(ImageEntry.COLUMN_IMAGE_HASH),
                        mImageValues.getAsString(ImageEntry.COLUMN_IMAGE_DATE)),
                null,
                null,
                null,
                null);
        assertEquals(imageHashDateCursor.getCount(), 1);
        validateCursor(imageHashDateCursor, mImageValues);
        imageHashDateCursor.close();
    }

    public void testTagIdUri() {
        Cursor tagCursor = mContext.getContentResolver().query(
                TagEntry.buildTagUri(mTagRowId),
                null,
                null,
                null,
                null);
        assertEquals(tagCursor.getCount(), 1);
        validateCursor(tagCursor, mTagValues);
        tagCursor.close();
    }

    public void testTagNameUri() {
        Cursor tagNameCursor = mContext.getContentResolver().query(
                TagEntry.buildTagName(mTagValues.getAsString(TagEntry.COLUMN_TAG_NAME)),
                null,
                null,
                null,
                null);
        assertEquals(tagNameCursor.getCount(), 1);
        validateCursor(tagNameCursor, mTagValues);
        tagNameCursor.close();
    }

    public void testImageTagUri() {
        Cursor imageTagCursor = mContext.getContentResolver().query(
                ImageTagEntry.buildTagNames(Arrays.asList(mTagValues.getAsString(TagEntry.COLUMN_TAG_NAME))),
                null,
                null,
                null,
                null);
        assertEquals(imageTagCursor.getCount(), 1);
        validateCursor(imageTagCursor, mImageValues);
        imageTagCursor.close();
    }

    static ContentValues createImageValues() {
        ContentValues imageValues = new ContentValues();
        imageValues.put(ImageEntry.COLUMN_IMAGE_URI, "/storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20141112-WA0000.jpg");
        imageValues.put(ImageEntry.COLUMN_IMAGE_HASH, "gh0hg30912hg091gh2q9g2390gj203290ghv");
        imageValues.put(ImageEntry.COLUMN_IMAGE_DATE, "20141219");
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
        return imageTagValues;
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
