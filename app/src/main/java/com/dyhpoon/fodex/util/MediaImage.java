package com.dyhpoon.fodex.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Process;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

/**
 * Created by darrenpoon on 16/2/15.
 */
public class MediaImage {

    public static void loadBitmapAsynchronously(ImageView imageView, Uri uri, int width, int height) {
        boolean isLocal = uri.toString().contains("content://");
        if (isLocal) {
            if (cancelPotentialWork(imageView, uri)) {
                final BitmapWorkerTask task = new BitmapWorkerTask(imageView, width, height);
                final AsyncDrawable asyncDrawable = new AsyncDrawable(imageView.getContext(), null, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(uri);
            }
        } else {
            Glide.with(imageView.getContext()).load(uri).override(width, height).into(imageView);
        }
    }

    public static Bitmap loadBitmapSynchronously(Context context,
                                                 Uri uri,
                                                 int reqWidth,
                                                 int reqHeight) {
        return loadBitmapSynchronously(context, uri, reqWidth, reqHeight, true);
    }

    public static Bitmap loadBitmapSynchronously(Context context,
                                                 Uri uri,
                                                 int reqWidth,
                                                 int reqHeight,
                                                 Boolean highResolution) {
        boolean isLocal = uri.toString().contains("content://");
        if (isLocal) {
            try {
                // First decode with inJustDecodeBounds=true to check dimensions
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                AssetFileDescriptor fd =
                        context.getContentResolver().openAssetFileDescriptor(uri, "r");
                BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, highResolution);

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                return BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                // When receive OOM, call GC, and try to get a smaller size of image.
                System.gc();
                return loadBitmapSynchronously(context, uri, reqWidth / 2, reqHeight / 2);
            }
        }
        return null;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth,
                                             int reqHeight,
                                             boolean highResolution) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            if (highResolution) {
                final int halfHeight = height/2;
                final int halfWidth = width/2;
                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            } else {
                final int tripeHeight = height*3;
                final int tripeWidth = width*3;
                while ((tripeHeight / inSampleSize) > reqHeight
                        || (tripeWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }
        }
        return inSampleSize;
    }

    private static class BitmapWorkerTask extends AsyncTask<Uri, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewWeakReference;
        private int mWidth, mHeight;
        private Uri uri;

        public BitmapWorkerTask(ImageView imageView, int width, int height) {
            imageViewWeakReference = new WeakReference<>(imageView);
            mWidth = width;
            mHeight = height;
        }

        @Override
        protected Bitmap doInBackground(Uri... params) {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
            if (imageViewWeakReference != null) {
                final ImageView imageView = imageViewWeakReference.get();
                if (imageView != null) {
                    uri = params[0];
                    return loadBitmapSynchronously(imageView.getContext(), uri, mWidth, mHeight);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (imageViewWeakReference != null && bitmap != null) {
                final ImageView imageView = imageViewWeakReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskWeakReference;

        public AsyncDrawable(Context context, Bitmap bitmap, BitmapWorkerTask task) {
            super(context.getResources(), bitmap);
            bitmapWorkerTaskWeakReference = new WeakReference<>(task);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskWeakReference.get();
        }
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private static boolean cancelPotentialWork(ImageView imageView, Uri uri) {
        final BitmapWorkerTask task = getBitmapWorkerTask(imageView);
        if (task != null) {
            if (task.uri == null || task.uri != uri) {
                task.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

}
