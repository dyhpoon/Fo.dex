package com.dyhpoon.fodex.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
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

    /* Inner class defines the table contents of image table */
    public static final class ImageEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_IMAGE).build();

        // Type
        public static final String CONTENT_ITEM_TYPE = "fodex.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_IMAGE;
        public static final String CONTENT_DIR_TYPE = "fodex.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_IMAGE;

        // Table Name
        public static final String TABLE_NAME = "image";

        // Columns
        public static final String COLUMN_IMAGE_ID = "photo_id";
        public static final String COLUMN_IMAGE_DATA = "data";
        public static final String COLUMN_IMAGE_DATE_TAKEN = "date_taken";

        // Building Uris
        // content://com.dyhpoon.fodex.provider/image/1
        public static Uri buildImageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
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
