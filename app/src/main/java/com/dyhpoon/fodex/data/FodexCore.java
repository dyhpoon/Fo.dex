package com.dyhpoon.fodex.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.net.Uri;
import android.provider.MediaStore;

import com.dyhpoon.fodex.data.FodexContract.ImageEntry;
import com.dyhpoon.fodex.data.FodexContract.ImageTagEntry;
import com.dyhpoon.fodex.data.FodexContract.IndexImageEntry;
import com.dyhpoon.fodex.data.FodexContract.ShareEntry;
import com.dyhpoon.fodex.data.FodexContract.TagEntry;
import com.dyhpoon.fodex.data.FodexContract.UnindexedImageEntry;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by darrenpoon on 16/2/15.
 */
public class FodexCore {

    public static List<FodexItem> getAllPhotoItems(Context context) {
        Cursor cursor = context.getContentResolver().query(
                ImageEntry.CONTENT_URI,
                null,
                null,
                null,
                ImageEntry.COLUMN_IMAGE_DATE_TAKEN + " DESC");

        List<FodexItem> items = convertCursorToItems(cursor);
        cursor.close();
        return items;
    }

    public static List<FodexItem> getIndexedPhotoItems(Context context) {
        Cursor cursor = context.getContentResolver().query(
                IndexImageEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        List<FodexItem> items = convertCursorToItems(cursor);
        cursor.close();
        return items;
    }

    public static List<FodexItem> getUnindexPhotoItems(Context context) {
        Cursor cursor = context.getContentResolver().query(
                UnindexedImageEntry.CONTENT_URI,
                null,
                null,
                null,
                ImageEntry.COLUMN_IMAGE_DATE_TAKEN + " DESC");

        List<FodexItem> items = convertCursorToItems(cursor);
        cursor.close();
        return items;
    }

    public static List<FodexItem> getSharedPhotoItems(Context context) {
        Cursor cursor = context.getContentResolver().query(
                ShareEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        List<FodexItem> items = convertCursorToItems(cursor);
        cursor.close();
        return items;
    }

    public static List<FodexItem> getSearchedPhotoItems(Context context, List<String> tagNames) {
        Cursor cursor = context.getContentResolver().query(
                ImageTagEntry.buildTagNames(tagNames),
                null,
                null,
                null,
                null);

        List<FodexItem> items = convertCursorToItems(cursor);
        cursor.close();
        return items;
    }

    public static List<String> getTags(Context context, long imageId) {
        Cursor cursor = context.getContentResolver().query(
                ImageTagEntry.buildSearchId(imageId),
                null,
                null,
                null,
                null);

        List<String> tags = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String tag = cursor.getString(cursor.getColumnIndex(TagEntry.COLUMN_TAG_NAME));
                tags.add(tag);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tags;
    }

    public static Cursor getMatchedTags(Context context, String words) {
        Cursor cursor = context.getContentResolver().query(
                TagEntry.buildSearchTagName(words),
                null,
                null,
                null,
                null);
        return cursor;
    }

    private static List<FodexItem> convertCursorToItems(Cursor cursor) {
        List<FodexItem> items = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                FodexItem item = new FodexItem(
                        cursor.getLong(cursor.getColumnIndex(ImageEntry._ID)),
                        cursor.getLong(cursor.getColumnIndex(ImageEntry.COLUMN_IMAGE_ID)),
                        cursor.getLong(cursor.getColumnIndex(ImageEntry.COLUMN_IMAGE_DATE_TAKEN)),
                        cursor.getString(cursor.getColumnIndex(ImageEntry.COLUMN_IMAGE_DATA))
                );
                items.add(item);
            } while (cursor.moveToNext());
        }
        return items;
    }

    public static void addTagsToPhotos(Context context, long[] imageIds, String[] tags) {
        List<ContentValues> bulkToInsert = new ArrayList<>();
        for (String tag : tags) {
            // get the id of tag first
            long tagId = getTagId(context, tag);

            // bulk insert into Image_Tag table
            for (long imageId : imageIds) {
                ContentValues values = new ContentValues();
                values.put(ImageTagEntry.COLUMN_IT_IMAGE_ID, imageId);
                values.put(ImageTagEntry.COLUMN_IT_TAG_ID, tagId);
                values.put(ImageTagEntry.COLUMN_IT_DATE_ADDED, System.currentTimeMillis());
                bulkToInsert.add(values);
            }
        }
        ContentValues[] serializedBulk = bulkToInsert.toArray(new ContentValues[bulkToInsert.size()]);
        context.getContentResolver().bulkInsert(ImageTagEntry.CONTENT_URI, serializedBulk);
    }

    public static void addSharePhotos(Context context, long[] imageIds) {
        List<ContentValues> bulkToInsert = new ArrayList<>();
        for (long imageId : imageIds) {
            ContentValues values = new ContentValues();
            values.put(ShareEntry.COLUMN_SHARE_IMAGE_ID, imageId);
            bulkToInsert.add(values);
        }
        ContentValues[] serializedBulk = bulkToInsert.toArray(new ContentValues[bulkToInsert.size()]);
        context.getContentResolver().bulkInsert(ShareEntry.CONTENT_URI, serializedBulk);
    }

    public static void deleteTagFromPhoto(Context context, long imageId, String tag) {
        ContentResolver resolver = context.getContentResolver();

        // get the id of tag first
        long tagId = getTagId(context, tag);

        // remove record
        int rowsDeleted = resolver.delete(
                ImageTagEntry.CONTENT_URI,
                ImageTagEntry.COLUMN_IT_IMAGE_ID + "= ? AND " + ImageTagEntry.COLUMN_IT_TAG_ID + "= ?",
                new String[]{String.valueOf(imageId), String.valueOf(tagId)}
        );

        // delete tag record if no records in image_tag points to it anymore
        if (rowsDeleted > 0) {
            Cursor cursor = resolver.query(
                    ImageTagEntry.buildSearchName(tag),
                    null,
                    null,
                    null,
                    null);
            if (!cursor.moveToFirst()) {
                // delete tag
                resolver.delete(
                        TagEntry.CONTENT_URI,
                        TagEntry.COLUMN_TAG_NAME + "=?",
                        new String[]{tag});
            }
            cursor.close();
        }
    }

    public static long getTagId(Context context, String tag) {
        ContentResolver resolver = context.getContentResolver();

        // check if tag exists in table
        Cursor cursor = resolver.query(
                TagEntry.buildTagName(tag),
                null,
                null,
                null,
                null);

        long tagId;
        if (cursor.moveToFirst()) {
            tagId = cursor.getLong(cursor.getColumnIndex(TagEntry._ID));
        } else {
            // otherwise insert into table
            ContentValues tagValues = new ContentValues();
            tagValues.put(TagEntry.COLUMN_TAG_NAME, tag);
            Uri tagUri = resolver.insert(TagEntry.CONTENT_URI, tagValues);
            tagId = ContentUris.parseId(tagUri);
        }
        cursor.close();
        return tagId;
    }

    public static void syncAllPhotos(final Context context) {
        ContentResolver resolver = context.getContentResolver();
        Cursor mediaCursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Images.Media._ID + " ASC");
        Cursor fodexCursor = resolver.query(
                ImageEntry.CONTENT_URI,
                null,
                null,
                null,
                ImageEntry.COLUMN_IMAGE_ID + " ASC");

        CursorJoiner joiner = new CursorJoiner(
                mediaCursor, new String[]{MediaStore.Images.Media._ID},
                fodexCursor, new String[]{ImageEntry.COLUMN_IMAGE_ID});

        List<ContentValues> insertContentValues = new ArrayList<>();
        List<ContentValues> removeContentValues = new ArrayList<>();

        for (CursorJoiner.Result result : joiner) {
            switch (result) {
                case LEFT: {
                    // add records from left
                    ContentValues values = new ContentValues();
                    values.put(ImageEntry.COLUMN_IMAGE_ID, mediaCursor.getLong(mediaCursor.getColumnIndex(MediaStore.Images.Media._ID)));
                    values.put(ImageEntry.COLUMN_IMAGE_DATA, mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                    values.put(ImageEntry.COLUMN_IMAGE_DATE_TAKEN, mediaCursor.getLong(mediaCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)));
                    insertContentValues.add(values);
                    break;
                }
                case RIGHT: {
                    // remove records from right
                    ContentValues values = new ContentValues();
                    values.put(ImageEntry.COLUMN_IMAGE_ID, fodexCursor.getLong(fodexCursor.getColumnIndex(ImageEntry.COLUMN_IMAGE_ID)));
                    values.put(ImageEntry.COLUMN_IMAGE_DATA, fodexCursor.getString(fodexCursor.getColumnIndex(ImageEntry.COLUMN_IMAGE_DATA)));
                    values.put(ImageEntry.COLUMN_IMAGE_DATE_TAKEN, fodexCursor.getLong(fodexCursor.getColumnIndex(ImageEntry.COLUMN_IMAGE_DATE_TAKEN)));
                    removeContentValues.add(values);
                    break;
                }
                case BOTH: {
                    // do nothing
                    break;
                }
            }
        }

        // delete
        for (ContentValues values : removeContentValues) {
            resolver.delete(ImageEntry.CONTENT_URI,
                    ImageEntry.COLUMN_IMAGE_ID + "=?",
                    new String[]{values.getAsString(ImageEntry.COLUMN_IMAGE_ID)});
        }

        // insert
        if (insertContentValues.size() > 0) {
            ContentValues[] bulkToInsert = new ContentValues[insertContentValues.size()];
            insertContentValues.toArray(bulkToInsert);
            resolver.bulkInsert(ImageEntry.CONTENT_URI, bulkToInsert);
        }

        mediaCursor.close();
        fodexCursor.close();
    }

}
