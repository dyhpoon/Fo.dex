package com.dyhpoon.fodex.data;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.provider.MediaStore;

import com.dyhpoon.fodex.util.OnCompleteListener;

import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;


/**
 * Created by darrenpoon on 16/2/15.
 */
public class FodexCursor {

    public static Cursor getAllPhotos(Context context) {
        return context.getContentResolver().query(
                FodexContract.ImageEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    public static void refreshAllPhotos(final Context context, final OnCompleteListener listener) {
        Task.call(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Cursor mediaCursor = MediaCursor.allPhotosCursor(context);
                Cursor fodexCursor = getAllPhotos(context);

                CursorJoiner joiner = new CursorJoiner(
                        mediaCursor, new String[] {MediaStore.Images.Media._ID},
                        fodexCursor, new String[] {FodexContract.ImageEntry.COLUMN_IMAGE_ID});

                for (CursorJoiner.Result result: joiner) {
                    switch (result) {
                        case LEFT: {
                            int imageId =
                                    mediaCursor.getInt(mediaCursor.getColumnIndex(MediaStore.Images.Media._ID));
                            // add id to table
                            break;
                        }
                        case RIGHT: {
                            int imageId =
                                    fodexCursor.getInt(fodexCursor.getColumnIndex(FodexContract.ImageEntry.COLUMN_IMAGE_ID));
                            // remove id from table
                            break;
                        }
                        case BOTH: {
                            // do nothing
                            break;
                        }
                    }
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
