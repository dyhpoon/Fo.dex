package com.dyhpoon.fodex.fullscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dyhpoon.fodex.data.actual.FodexImageContract;
import com.dyhpoon.fodex.util.MediaImage;
import com.dyhpoon.fodex.view.TouchImageView;

import java.util.Stack;


/**
 * Created by darrenpoon on 26/1/15.
 */
public abstract class ReusableFullscreenAdapter extends PagerAdapter {

    public TouchImageView currentView = null;

    public abstract Uri imageUriAtPosition(int position);
    public abstract void onLongClick(int position);
    public abstract void onClick(int position);

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
    public Object instantiateItem(ViewGroup container, final int position) {
        final ImageView photoView = (ImageView) createOrRecycleView(mContext);
        Uri uri = imageUriAtPosition(position);

        // load bitmap in main thread if the image is in local disk.
        if (uri.toString().contains("content")) {
            Bitmap bitmap = MediaImage.loadBitmapSynchronously(photoView.getContext(), uri, mWidth, mHeight);
            photoView.setImageBitmap(bitmap);
        }
        // otherwise, load image asynchronously
        else {
            MediaImage.loadBitmapAsynchronously(photoView, uri, mWidth, mHeight);
        }
        container.addView(photoView, 0);
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ReusableFullscreenAdapter.this.onLongClick(position);
                return true;
            }
        });
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReusableFullscreenAdapter.this.onClick(position);
            }
        });
        return photoView;
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
