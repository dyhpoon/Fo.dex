package com.dyhpoon.fodex.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.dyhpoon.fodex.data.FodexContract.ImageEntry;
import com.dyhpoon.fodex.data.FodexContract.TagEntry;
import com.dyhpoon.fodex.data.FodexContract.ImageTagEntry;

import java.util.HashMap;
import java.util.List;

/**
 * Created by darrenpoon on 20/12/14.
 */
public class FodexProvider extends ContentProvider {

    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private FodexDbHelper mOpenHelper;

    private static final int IMAGE_ID = 100;
    private static final int IMAGE_HASH = 101;
    private static final int IMAGE_HASH_WITH_DATE = 102;
    private static final int TAG_ID = 200;
    private static final int TAG_SEARCH = 201;
    private static final int TAG_KEYWORDS = 202;

    private static final String ERR_UNSUPPORTED_URI = "Unsupported uri: ";

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FodexContract.CONTENT_AUTHORITY;

        final String imagePath = FodexContract.PATH_IMAGE;
        // content://com.dyhpoon.fodex.provider/image/1
        matcher.addURI(authority, imagePath + "/#", IMAGE_ID);
        // content://com.dyhpoon.fodex.provider/image/hash/gh390hg223g
        matcher.addURI(authority, imagePath + "/" + ImageEntry.COLUMN_IMAGE_HASH + "/*", IMAGE_HASH);
        // content://com.dyhpoon.fodex.provider/image/hash/gh390hg223g/date/20141219
        matcher.addURI(authority, imagePath + "/" + ImageEntry.COLUMN_IMAGE_HASH + "/*/" + ImageEntry.COLUMN_IMAGE_DATE + "/*", IMAGE_HASH_WITH_DATE);

        final String tagPath = FodexContract.PATH_TAG;
        // content://com.dyhpoon.fodex.provider/tag/1
        matcher.addURI(authority, tagPath + "/#", TAG_ID);
        // content://com.dyhpoon.fodex.provider/tag/search/morning
        matcher.addURI(authority, tagPath + "/" + TagEntry.PATH_SEGMENT_SEARCH + "/*", TAG_SEARCH);
        // content://com.dyhpoon.fodex.provider/tag/keywords/morning+evening
        matcher.addURI(authority, tagPath + "/" + TagEntry.PATH_SEGMENT_KEYWORD + "/*", TAG_KEYWORDS);

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
            case IMAGE_HASH:
                cursor = mOpenHelper.getReadableDatabase().query(
                        ImageEntry.TABLE_NAME,
                        projection,
                        ImageEntry.COLUMN_IMAGE_HASH + " = '" + ImageEntry.getHashFromUri(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case IMAGE_HASH_WITH_DATE:
                cursor = mOpenHelper.getReadableDatabase().query(
                        ImageEntry.TABLE_NAME,
                        projection,
                        ImageEntry.COLUMN_IMAGE_HASH + " = '" + ImageEntry.getHashFromUri(uri) + "'" +
                        "AND " + ImageEntry.COLUMN_IMAGE_DATE+ " = '" + ImageEntry.getDateFromUri(uri) + "'",
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
                    put(ImageEntry.COLUMN_IMAGE_URI, ImageEntry.COLUMN_IMAGE_URI);
                    put(ImageEntry.COLUMN_IMAGE_HASH, ImageEntry.COLUMN_IMAGE_HASH);
                    put(ImageEntry.COLUMN_IMAGE_DATE, ImageEntry.COLUMN_IMAGE_DATE);
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
            default:
                throw new UnsupportedOperationException(ERR_UNSUPPORTED_URI + uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case IMAGE_ID:
                return ImageEntry.CONTENT_ITEM_TYPE;
            case IMAGE_HASH:
                return ImageEntry.CONTENT_ITEM_TYPE;
            case IMAGE_HASH_WITH_DATE:
                return ImageEntry.CONTENT_ITEM_TYPE;
            case TAG_ID:
                return TagEntry.CONTENT_ITEM_TYPE;
            case TAG_SEARCH:
                return TagEntry.CONTENT_DIR_TYPE;
            case TAG_KEYWORDS:
                return ImageEntry.CONTENT_DIR_TYPE;
            default:
                throw new UnsupportedOperationException(ERR_UNSUPPORTED_URI + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
