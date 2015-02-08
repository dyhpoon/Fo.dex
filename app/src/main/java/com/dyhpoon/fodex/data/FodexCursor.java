package com.dyhpoon.fodex.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by darrenpoon on 28/1/15.
 */
public class FodexCursor {

    public static Cursor allPhotosCursor(Context context) {
        ContentResolver resolver = context.getContentResolver();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
        };
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        );
        return cursor;
    }

    public static Uri getThumbnail(Context context, int imageId) {
        ContentResolver resolver = context.getContentResolver();
        String[] projection = new String[] {
                MediaStore.Images.Thumbnails._ID
        };
        Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                resolver,
                imageId,
                MediaStore.Images.Thumbnails.MINI_KIND,
                projection);
        final int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
        if (cursor.moveToFirst()) {
            final String id = cursor.getString(idColumn);
            cursor.close();
            return ContentUris.withAppendedId(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                    Long.parseLong(id));
        } else {
            return null;
        }
    }

}
