package com.dyhpoon.fodex.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.provider.MediaStore;

import com.dyhpoon.fodex.data.FodexContract.ImageEntry;
import com.dyhpoon.fodex.util.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;


/**
 * Created by darrenpoon on 16/2/15.
 */
public class FodexCursor {

    public static List<FodexItem> getAllPhotoItems(Context context) {
        Cursor cursor = context.getContentResolver().query(
                ImageEntry.CONTENT_URI,
                null,
                null,
                null,
                ImageEntry.COLUMN_IMAGE_DATE_TAKEN + " DESC");

        List<FodexItem> items = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                FodexItem item = new FodexItem(
                        cursor.getInt(cursor.getColumnIndex(ImageEntry.COLUMN_IMAGE_ID)),
                        cursor.getLong(cursor.getColumnIndex(ImageEntry.COLUMN_IMAGE_DATE_TAKEN)),
                        cursor.getString(cursor.getColumnIndex(ImageEntry.COLUMN_IMAGE_DATA))
                );
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public static void syncAllPhotos(final Context context, final OnCompleteListener listener) {
        Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
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
                        mediaCursor, new String[] {MediaStore.Images.Media._ID},
                        fodexCursor, new String[] {ImageEntry.COLUMN_IMAGE_ID});

                List<ContentValues> insertContentValues = new ArrayList<>();
                List<ContentValues> removeContentValues = new ArrayList<>();

                for (CursorJoiner.Result result: joiner) {
                    switch (result) {
                        case LEFT: {
                            // add records from left
                            ContentValues values = new ContentValues();
                            values.put(ImageEntry.COLUMN_IMAGE_ID, mediaCursor.getInt(mediaCursor.getColumnIndex(MediaStore.Images.Media._ID)));
                            values.put(ImageEntry.COLUMN_IMAGE_DATA, mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                            values.put(ImageEntry.COLUMN_IMAGE_DATE_TAKEN, mediaCursor.getLong(mediaCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)));
                            insertContentValues.add(values);
                            break;
                        }
                        case RIGHT: {
                            // remove records from right
                            ContentValues values = new ContentValues();
                            values.put(ImageEntry.COLUMN_IMAGE_ID, fodexCursor.getInt(fodexCursor.getColumnIndex(ImageEntry.COLUMN_IMAGE_ID)));
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
                for (ContentValues values: removeContentValues) {
                    resolver.delete(ImageEntry.CONTENT_URI,
                            ImageEntry.COLUMN_IMAGE_ID + "=?",
                            new String[] {values.getAsString(ImageEntry.COLUMN_IMAGE_ID)});
                }

                // insert
                if (insertContentValues.size() > 0) {
                    ContentValues[] bulkToInsert = new ContentValues[insertContentValues.size()];
                    insertContentValues.toArray(bulkToInsert);
                    resolver.bulkInsert(ImageEntry.CONTENT_URI, bulkToInsert);
                }

                mediaCursor.close();
                fodexCursor.close();
                return null;
            }
        }).continueWith(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                if (task.isFaulted()) {
                    listener.didFail();
                } else {
                    listener.didComplete();
                }
                return null;
            }
        });
    }


}
