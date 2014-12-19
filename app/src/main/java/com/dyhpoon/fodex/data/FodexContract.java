package com.dyhpoon.fodex.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    }

    /* Inner class defines the table contents of tag table */
    public static final class TagEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TAG).build();

        // Table Name
        public static final String TABLE_NAME = "tag";

        // Columns
        public static final String COLUMN_TAG_NAME = "name";

    }

    /* Inner class defines the table contents of index table */
    public static final class ImageTagEntry implements BaseColumns {

        public static final String TABLE_NAME = "image_tag";

        // Columns
        public static final String COLUMN_IT_IMAGE_ID = "image_id";
        public static final String COLUMN_IT_TAG_ID = "tag_id";
    }
}
