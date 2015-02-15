package com.dyhpoon.fodex.fullscreen;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dyhpoon.fodex.data.FodexImageContract;
import com.dyhpoon.fodex.view.TouchImageView;

import java.io.FileNotFoundException;
import java.util.Stack;


/**
 * Created by darrenpoon on 26/1/15.
 */
public abstract class ReusableFullscreenAdapter extends PagerAdapter {

    public TouchImageView currentView = null;

    public abstract Uri imageUriAtPosition(int position);

    private Context mContext;
    private int mWidth, mHeight;
    private Stack<View> mRecycledViews = new Stack<>();

    public ReusableFullscreenAdapter(Context context) {
        mContext = context;
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, final Object object) {
        currentView = (TouchImageView)object;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ImageView photoView = (ImageView) createOrRecycleView(mContext);
        Bitmap bm = decodeSampledBitmapFromResource(imageUriAtPosition(position), mWidth, mHeight);
        photoView.setImageBitmap(bm);
        container.addView(photoView, 0);
        return photoView;
    }

    private Bitmap decodeSampledBitmapFromResource(Uri uri, int reqWidth, int reqHeight) {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            AssetFileDescriptor fd =
                    mContext.getContentResolver().openAssetFileDescriptor(uri, "r");
            BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View recycledView = (View) object;
        container.removeView(recycledView);
        cleanView((TouchImageView)recycledView);
        mRecycledViews.push(recycledView);
    }

    private View createOrRecycleView(Context context) {
        View photoView;
        if (mRecycledViews.isEmpty()) {
            photoView = new TouchImageView(context);
            photoView.setBackgroundColor(Color.BLACK);
            photoView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            photoView.setMinimumHeight(FodexImageContract.preferredMinimumHeight(mContext));
        } else {
            photoView = mRecycledViews.pop();
        }
        photoView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        return photoView;
    }

    private void cleanView(TouchImageView v) {
        v.setLayerType(View.LAYER_TYPE_NONE, null);
        v.setImageDrawable(null);
        v.resetZoom();
    }
}
