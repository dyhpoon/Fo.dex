package com.dyhpoon.fodex.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by darrenpoon on 14/12/14.
 */
public class FodexContract {

    // Name of the Content Provider
    public static final String CONTENT_AUTHORITY = "com.dyhpoon.fodex.provider";

    // Base Uri to our Content Provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible path
    public static final String PATH_IMAGE = "image";
    public static final String PATH_TAG = "tag";

    // Date Format
    public static final String DATE_FORMAT = "yyyMMdd";
    public static String getDbDateString(Date date) {
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }
    public static Date getDateFromDb(String dateText) {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(dateText);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* Inner class defines the table contents of image table */
    public static final class ImageEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_IMAGE).build();

        // Table Name
        public static final String TABLE_NAME = "image";

        // Columns
        public static final String COLUMN_IMAGE_URI = "uri";
        public static final String COLUMN_IMAGE_HASH = "hash";
        public static final String COLUMN_IMAGE_DATE = "date";

        // Building Uris
        // content://com.dyhpoon.fodex.provider/image/1
        public static Uri buildImageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // content://com.dyhpoon.fodex.provider/image/gh390hg223g
        public static Uri buildHash(String hash) {
            return CONTENT_URI.buildUpon().appendPath(hash).build();
        }

        // content://com.dyhpoon.fodex.provider/image/gh390hg223g?date=20141219
        public static Uri buildHashWithDate(String hash, String date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(hash)
                    .appendQueryParameter(COLUMN_IMAGE_DATE, date)
                    .build();
        }

        // content://com.dyhpoon.fodex.provider/image/gh390hg223g => gh390hg223g
        public static String getHashFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        // content://com.dyhpoon.fodex.provider/image/gh390hg223g?date=20141219 => 20141219
        public static String getDateFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_IMAGE_DATE);
        }

    }

    /* Inner class defines the table contents of tag table */
    public static final class TagEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TAG).build();

        // Table Name
        public static final String TABLE_NAME = "tag";

        // Columns
        public static final String COLUMN_TAG_NAME = "name";

        // Building Uris
        // content://com.dyhpoon.fodex.provider/tag/1
        public static Uri buildTagUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // content://com.dyhpoon.fodex.provider/tag/morning
        public static Uri buildTagName(String tagName) {
            return CONTENT_URI.buildUpon().appendPath(tagName).build();
        }

        // content://com.dyhpoon.fodex.provider/tag?name=morning+evening
        public static  Uri buildMultipleTagNames(List<String> names) {
            String appendedString = TextUtils.join("+", names);
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(COLUMN_TAG_NAME, appendedString)
                    .build();
        }

        // content://com.dyhpoon.fodex.provider/tag/morning => morning
        public static String getTagName(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        // content://com.dyhpoon.fodex.provider/tag?name=morning+evening => [morning, evening]
        public static List<String> getMultipleTagNames(Uri uri) {
            String fetchedString = uri.getQueryParameter(COLUMN_TAG_NAME);
            return Arrays.asList(TextUtils.split(fetchedString, "\\+"));
        }

    }

    /* Inner class defines the table contents of index table */
    public static final class ImageTagEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "image_tag";

        // Columns
        public static final String COLUMN_IT_IMAGE_ID = "image_id";
        public static final String COLUMN_IT_TAG_ID = "tag_id";

    }
}
