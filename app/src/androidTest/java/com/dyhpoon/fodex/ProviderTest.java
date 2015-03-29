package com.dyhpoon.fodex;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.dyhpoon.fodex.data.actual.FodexContract.ImageEntry;
import com.dyhpoon.fodex.data.actual.FodexContract.ImageTagEntry;
import com.dyhpoon.fodex.data.actual.FodexContract.ShareEntry;
import com.dyhpoon.fodex.data.actual.FodexContract.TagEntry;

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
    private long mShareRowId = -1;
    private ContentValues mImageValues = createImageValues();
    private ContentValues mTagValues = createTagValues();
    private ContentValues mImageTagValues;
    private ContentValues mShareValues;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // TODO: test update
        // TODO: test delete
        // TODO: test bulk insert
        mContext.getContentResolver().delete(ImageTagEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(TagEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(ImageEntry.CONTENT_URI, null, null);

        Uri imageUri = mContext.getContentResolver().insert(ImageEntry.CONTENT_URI, mImageValues);
        mImageRowId = ContentUris.parseId(imageUri);
        assertTrue(mImageRowId != -1);

        Uri tagUri = mContext.getContentResolver().insert(TagEntry.CONTENT_URI, mTagValues);
        mTagRowId = ContentUris.parseId(tagUri);
        assertTrue(mTagRowId != -1);

        mImageTagValues = createImageTagValues(mImageRowId, mTagRowId);
        assertNotNull(mImageTagValues);

        Uri imageTagUri = mContext.getContentResolver().insert(ImageTagEntry.CONTENT_URI, mImageTagValues);
        mImageTagRowId = ContentUris.parseId(imageTagUri);
        assertTrue(mImageRowId != -1);

        mShareValues = createShareValues(mImageRowId);
        assertNotNull(mShareValues);

        Uri shareUri = mContext.getContentResolver().insert(ShareEntry.CONTENT_URI, mShareValues);
        mShareRowId = ContentUris.parseId(shareUri);
        assertTrue(mShareRowId != -1);
    }

    public void testImageUri() {
        Cursor imageCursor = mContext.getContentResolver().query(
                ImageEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertEquals(1, imageCursor.getCount());
        validateCursor(imageCursor, mImageValues);
        imageCursor.close();
    }

    public void testImageIdUri() {
        Cursor imageCursor = mContext.getContentResolver().query(
                ImageEntry.buildImageUri(mImageRowId),
                null,
                null,
                null,
                null);
        assertEquals(1, imageCursor.getCount());
        validateCursor(imageCursor, mImageValues);
        imageCursor.close();
    }

    public void testTagUri() {
        Cursor tagCursor = mContext.getContentResolver().query(
                TagEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertEquals(1, tagCursor.getCount());
        validateCursor(tagCursor, mTagValues);
        tagCursor.close();
    }

    public void testTagIdUri() {
        Cursor tagCursor = mContext.getContentResolver().query(
                TagEntry.buildTagUri(mTagRowId),
                null,
                null,
                null,
                null);
        assertEquals(1, tagCursor.getCount());
        validateCursor(tagCursor, mTagValues);
        tagCursor.close();
    }

    public void testTagNameUri() {
        Cursor tagNameCursor = mContext.getContentResolver().query(
                TagEntry.buildSearchTagName(mTagValues.getAsString(TagEntry.COLUMN_TAG_NAME)),
                null,
                null,
                null,
                null);
        assertEquals(1, tagNameCursor.getCount());
        validateCursor(tagNameCursor, mTagValues);
        tagNameCursor.close();
    }

    public void testImageTagUri() {
        Cursor imageTagCursor = mContext.getContentResolver().query(
                ImageTagEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertEquals(1, imageTagCursor.getCount());
        validateCursor(imageTagCursor, mImageTagValues);
        imageTagCursor.close();
    }

    public void testImageTagIdUri() {
        Cursor imageTagCursor = mContext.getContentResolver().query(
                ImageTagEntry.buildImageTagUri(mImageTagRowId),
                null,
                null,
                null,
                null);
        assertEquals(1, imageTagCursor.getCount());
        validateCursor(imageTagCursor, mImageTagValues);
        imageTagCursor.close();
    }

    public void testImageTagWithTagNamesUri() {
        Cursor imageTagCursor = mContext.getContentResolver().query(
                ImageTagEntry.buildTagNames(Arrays.asList(mTagValues.getAsString(TagEntry.COLUMN_TAG_NAME))),
                null,
                null,
                null,
                null);
        assertEquals(1, imageTagCursor.getCount());
        validateCursor(imageTagCursor, mImageValues);
        imageTagCursor.close();
    }

    public void testShareUri() {
        Cursor shareCursor = mContext.getContentResolver().query(
                ShareEntry.buildShareUri(mShareRowId),
                null,
                null,
                null,
                null);
        assertEquals(1, shareCursor.getCount());
        validateCursor(shareCursor, mShareValues);
        shareCursor.close();
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
        shareValues.put(ShareEntry.COLUMN_SHARE_IMAGE_ID, imageRowId);
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
