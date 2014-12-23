package com.dyhpoon.fodex.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by darrenpoon on 14/12/14.
 */
public class FodexContract {

    public static final String ERR_INVALID_URI = "The Uri provided is invalid: ";

    // Name of the Content Provider
    public static final String CONTENT_AUTHORITY = "com.dyhpoon.fodex.provider";

    // Base Uri to our Content Provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible path
    public static final String PATH_IMAGE = "image";
    public static final String PATH_TAG = "tag";
    public static final String PATH_IMAGE_TAG = "image_tag";

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

        // Type
        public static final String CONTENT_ITEM_TYPE = "fodex.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_IMAGE;
        public static final String CONTENT_DIR_TYPE = "fodex.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_IMAGE;

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

        // content://com.dyhpoon.fodex.provider/image/hash/gh390hg223g
        public static Uri buildHash(String hash) {
            return CONTENT_URI.buildUpon()
                    .appendPath(COLUMN_IMAGE_HASH)
                    .appendPath(hash)
                    .build();
        }

        // content://com.dyhpoon.fodex.provider/image/hash/gh390hg223g/date/20141219
        public static Uri buildHashWithDate(String hash, String date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(COLUMN_IMAGE_HASH)
                    .appendPath(hash)
                    .appendPath(COLUMN_IMAGE_DATE)
                    .appendPath(date)
                    .build();
        }

        // content://com.dyhpoon.fodex.provider/image/hash/gh390hg223g => gh390hg223g
        public static String getHashFromUri(Uri uri) {
            if (!uri.getPathSegments().get(0).equals(TABLE_NAME) || !uri.getPathSegments().get(1).equals(COLUMN_IMAGE_HASH))
                throw new IllegalArgumentException(ERR_INVALID_URI + uri);
            return uri.getPathSegments().get(2);
        }

        // content://com.dyhpoon.fodex.provider/image/hash/gh390hg223g/date/20141219 => 20141219
        public static String getDateFromUri(Uri uri) {
            if (!uri.getPathSegments().get(0).equals(TABLE_NAME) || !uri.getPathSegments().get(1).equals(COLUMN_IMAGE_HASH) || !uri.getPathSegments().get(3).equals(COLUMN_IMAGE_DATE))
                throw new IllegalArgumentException(ERR_INVALID_URI + uri);
            return uri.getPathSegments().get(4);
        }

    }

    /* Inner class defines the table contents of tag table */
    public static final class TagEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TAG).build();

        // Type
        public static final String CONTENT_ITEM_TYPE = "fodex.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_TAG;
        public static final String CONTENT_DIR_TYPE = "fodex.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_TAG;

        // Table Name
        public static final String TABLE_NAME = "tag";

        // Columns
        public static final String COLUMN_TAG_NAME = "name";
        public static final String COLUMN_TAG_VISIBLE = "visible";

        // Path Segments
        public static final String PATH_SEGMENT_SEARCH = "search";
        public static final String PATH_SEGMENT_KEYWORD = "keywords";

        // Building Uris
        // content://com.dyhpoon.fodex.provider/tag/1
        public static Uri buildTagUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // content://com.dyhpoon.fodex.provider/tag/search/morning
        public static Uri buildTagName(String tagName) {
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_SEGMENT_SEARCH)
                    .appendPath(tagName)
                    .build();
        }

        // content://com.dyhpoon.fodex.provider/tag/search/morning => morning
        public static String getTagName(Uri uri) {
            if (!uri.getPathSegments().get(0).equals(TABLE_NAME) || !uri.getPathSegments().get(1).equals(PATH_SEGMENT_SEARCH))
                throw new IllegalArgumentException(ERR_INVALID_URI + uri);
            return uri.getPathSegments().get(2);
        }

    }

    /* Inner class defines the table contents of index table */
    public static final class ImageTagEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_IMAGE_TAG).build();

        // Type
        public static final String CONTENT_ITEM_TYPE = "fodex.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_IMAGE_TAG;
        public static final String CONTENT_DIR_TYPE = "fodex.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_IMAGE_TAG;

        // Table name
        public static final String TABLE_NAME = "image_tag";

        // Columns
        public static final String COLUMN_IT_IMAGE_ID = "image_id";
        public static final String COLUMN_IT_TAG_ID = "tag_id";

        // Building Uris
        // content://com.dyhpoon.fodex.provider/image_tag/1
        public static Uri buildImageTagUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // content://com.dyhpoon.fodex.provider/tag/keywords/morning+evening
        public static Uri buildTagNames(List<String> names) {
            String appendedString = serializeNames(names, "+");
            return TagEntry.CONTENT_URI.buildUpon()
                    .appendPath(TagEntry.PATH_SEGMENT_KEYWORD)
                    .appendPath(appendedString)
                    .build();
        }

        // content://com.dyhpoon.fodex.provider/tag/keywords/morning+evening => [morning, evening]
        public static List<String> getTagNames(Uri uri) {
            if (!uri.getPathSegments().get(0).equals(TagEntry.TABLE_NAME) || !uri.getPathSegments().get(1).equals(TagEntry.PATH_SEGMENT_KEYWORD))
                throw new IllegalArgumentException(ERR_INVALID_URI + uri);
            String fetchedString = uri.getPathSegments().get(2);
            return deserializeNames(fetchedString, "\\+");
        }

        private static String serializeNames(List<String> names, CharSequence delimiter) {
            HashSet<String> encodedNames = new HashSet<String>();
            for (String name : names) {
                String encodedName = null;
                try {
                    encodedName = URLEncoder.encode(name, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (encodedName != null && encodedName.length() > 0)
                    encodedNames.add(encodedName);
            }
            return TextUtils.join(delimiter, encodedNames);
        }

        private static List<String> deserializeNames(String string, String expression) {
            String[] names = TextUtils.split(string, expression);
            HashSet<String> decodedNames = new HashSet<String>();
            for (String name : names) {
                String decodedName = null;
                try {
                    decodedName = URLDecoder.decode(name, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (decodedName != null && decodedName.length() > 0)
                    decodedNames.add(decodedName);
            }
            return new ArrayList<String>(decodedNames);
        }

    }
}
