package com.dyhpoon.fodex.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.dyhpoon.fodex.data.FodexContract.ImageEntry;
import com.dyhpoon.fodex.data.FodexContract.ImageTagEntry;
import com.dyhpoon.fodex.data.FodexContract.TagEntry;

import java.util.HashMap;
import java.util.List;

/**
 * Created by darrenpoon on 20/12/14.
 */
public class FodexProvider extends ContentProvider {

    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private FodexDbHelper mOpenHelper;

    private static final int IMAGE = 100;
    private static final int IMAGE_ID = 101;

    private static final int TAG = 200;
    private static final int TAG_ID = 201;
    private static final int TAG_SEARCH = 202;
    private static final int TAG_KEYWORDS = 203;

    private static final int IMAGE_TAG = 300;
    private static final int IMAGE_TAG_ID = 301;

    private static final String ERR_UNSUPPORTED_URI = "Unsupported uri: ";
    private static final String ERR_INSERT_FAILED = "Failed to insert row into: ";

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FodexContract.CONTENT_AUTHORITY;

        final String imagePath = FodexContract.PATH_IMAGE;
        // content://com.dyhpoon.fodex.provider/image
        matcher.addURI(authority, imagePath, IMAGE);
        // content://com.dyhpoon.fodex.provider/image/1
        matcher.addURI(authority, imagePath + "/#", IMAGE_ID);

        final String tagPath = FodexContract.PATH_TAG;
        // content://com.dyhpoon.fodex.provider/tag
        matcher.addURI(authority, tagPath, TAG);
        // content://com.dyhpoon.fodex.provider/tag/1
        matcher.addURI(authority, tagPath + "/#", TAG_ID);
        // content://com.dyhpoon.fodex.provider/tag/search/morning
        matcher.addURI(authority, tagPath + "/" + TagEntry.PATH_SEGMENT_SEARCH + "/*", TAG_SEARCH);
        // content://com.dyhpoon.fodex.provider/tag/keywords/morning+evening
        matcher.addURI(authority, tagPath + "/" + TagEntry.PATH_SEGMENT_KEYWORD + "/*", TAG_KEYWORDS);

        final String imageTagPath = FodexContract.PATH_IMAGE_TAG;
        // content://com.dyhpoon.fodex.provider/tag
        matcher.addURI(authority, imageTagPath, IMAGE_TAG);
        // content://com.dyhpoon.fodex.provider/tag/1
        matcher.addURI(authority, imageTagPath + "/#", IMAGE_TAG_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FodexDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (mUriMatcher.match(uri)) {
            case IMAGE:
                cursor = mOpenHelper.getReadableDatabase().query(
                        ImageEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case IMAGE_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        ImageEntry.TABLE_NAME,
                        projection,
                        ImageEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TAG:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TagEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TAG_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TagEntry.TABLE_NAME,
                        projection,
                        TagEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TAG_SEARCH:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TagEntry.TABLE_NAME,
                        projection,
                        TagEntry.COLUMN_TAG_NAME + " LIKE '%" + TagEntry.getTagName(uri) + "%'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TAG_KEYWORDS:
                /*
SELECT image._id, image.uri, image.hash, image.date
FROM image_tag
INNER JOIN image ON image._id = image_tag.image_id
INNER JOIN tag ON tag._id = image_tag.tag_id
WHERE tag.name in ("hello")
group by image._id
having count(tag.name) = 1

SELECT image._id, image.uri, image.hash, image.date
FROM image_tag
INNER JOIN image ON image._id = image_tag.image_id
INNER JOIN tag ON tag._id = image_tag.tag_id
WHERE tag.name in ("hello", "bye")
group by image._id
having count(tag.name) = 2
                 */
                final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

                builder.setTables(ImageTagEntry.TABLE_NAME +
                        " INNER JOIN " + TagEntry.TABLE_NAME + " ON " + TagEntry.TABLE_NAME + "." + TagEntry._ID + " = " + ImageTagEntry.TABLE_NAME + "." + ImageTagEntry.COLUMN_IT_TAG_ID +
                        " INNER JOIN " + ImageEntry.TABLE_NAME + " ON " + ImageEntry.TABLE_NAME + "." + ImageEntry._ID + " = " + ImageTagEntry.TABLE_NAME + "." + ImageTagEntry.COLUMN_IT_IMAGE_ID);

                builder.setProjectionMap(new HashMap<String, String>(){{
                    put(ImageEntry.TABLE_NAME + "." + ImageEntry._ID, ImageEntry.TABLE_NAME + "." + ImageEntry._ID);
                    put(ImageEntry.COLUMN_IMAGE_ID, ImageEntry.COLUMN_IMAGE_ID);
                    put(ImageEntry.COLUMN_IMAGE_DATA, ImageEntry.COLUMN_IMAGE_DATA);
                    put(ImageEntry.COLUMN_IMAGE_DATE_TAKEN, ImageEntry.COLUMN_IMAGE_DATE_TAKEN);
                }});

                List<String> tagNames = ImageTagEntry.getTagNames(uri);
                if (tagNames.size() > 0) {
                    builder.appendWhere(TagEntry.TABLE_NAME + "." + TagEntry.COLUMN_TAG_NAME + " in (");
                    for (int i = 0; i < tagNames.size(); i++) {
                        String tagName = tagNames.get(i);
                        if (i == tagNames.size() - 1) {
                            builder.appendWhereEscapeString(tagName);
                        } else {
                            builder.appendWhereEscapeString(tagName);
                            builder.appendWhere(",");
                        }
                    }
                    builder.appendWhere(")");
                }

                cursor = builder.query(
                        mOpenHelper.getWritableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        ImageEntry.TABLE_NAME + "." + ImageEntry._ID,
                        "count(" + TagEntry.TABLE_NAME + "." + TagEntry.COLUMN_TAG_NAME + ") = " + Integer.toString(tagNames.size()),
                        sortOrder);
                break;
            case IMAGE_TAG:
                cursor = mOpenHelper.getReadableDatabase().query(
                        ImageTagEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case IMAGE_TAG_ID:
                cursor = mOpenHelper.getReadableDatabase().query(
                        ImageTagEntry.TABLE_NAME,
                        projection,
                        ImageTagEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException(ERR_UNSUPPORTED_URI + uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case IMAGE:
                return ImageEntry.CONTENT_DIR_TYPE;
            case IMAGE_ID:
                return ImageEntry.CONTENT_ITEM_TYPE;
            case TAG:
                return TagEntry.CONTENT_DIR_TYPE;
            case TAG_ID:
                return TagEntry.CONTENT_ITEM_TYPE;
            case TAG_SEARCH:
                return TagEntry.CONTENT_DIR_TYPE;
            case TAG_KEYWORDS:
                return ImageEntry.CONTENT_DIR_TYPE;
            case IMAGE_TAG:
                return ImageTagEntry.CONTENT_DIR_TYPE;
            case IMAGE_TAG_ID:
                return ImageTagEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException(ERR_UNSUPPORTED_URI + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (mUriMatcher.match(uri)) {
            case IMAGE: {
                long _id = database.insert(ImageEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ImageEntry.buildImageUri(_id);
                else
                    throw new SQLException(ERR_INSERT_FAILED + uri);
                break;
            }
            case TAG: {
                long _id = database.insert(TagEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = TagEntry.buildTagUri(_id);
                else
                    throw new SQLException(ERR_INSERT_FAILED + uri);
                break;
            }
            case IMAGE_TAG: {
                long _id = database.insert(ImageTagEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ImageTagEntry.buildImageTagUri(_id);
                else
                    throw new SQLException(ERR_INSERT_FAILED + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException(ERR_UNSUPPORTED_URI + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case IMAGE: {
                return bulkInsertHelper(ImageEntry.TABLE_NAME, database, uri, values);
            }
            case TAG: {
                return bulkInsertHelper(TagEntry.TABLE_NAME, database, uri, values);
            }
            case IMAGE_TAG: {
                return bulkInsertHelper(ImageTagEntry.TABLE_NAME, database, uri, values);
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private int bulkInsertHelper(String table,
                                 SQLiteDatabase database,
                                 Uri uri,
                                 ContentValues[] values) {
        database.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {

                long _id = database.insert(table, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        int rowsDeleted;
        switch (mUriMatcher.match(uri)) {
            case IMAGE:
                rowsDeleted = database.delete(ImageEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TAG:
                rowsDeleted = database.delete(TagEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case IMAGE_TAG:
                rowsDeleted = database.delete(ImageTagEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(ERR_UNSUPPORTED_URI + uri);
        }
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        int rowsUpdated;
        switch (mUriMatcher.match(uri)) {
            case IMAGE:
                rowsUpdated = database.update(ImageEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TAG:
                rowsUpdated = database.update(TagEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case IMAGE_TAG:
                rowsUpdated = database.update(ImageTagEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(ERR_UNSUPPORTED_URI + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
