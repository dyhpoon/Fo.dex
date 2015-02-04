package com.dyhpoon.fodex.fullscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dyhpoon.fodex.data.FodexImageContract;
import com.dyhpoon.fodex.view.TouchImageView;

import java.util.Stack;


/**
 * Created by darrenpoon on 26/1/15.
 */
public abstract class ReusableFullscreenAdapter extends PagerAdapter {

    public TouchImageView currentView = null;

    public abstract Bitmap imageBitmapAtPosition(int position);

    private Context mContext;
    private Stack<View> mRecycledViews = new Stack<View>();

    public ReusableFullscreenAdapter(Context context) {
        mContext = context;
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
        ImageView photoView = (ImageView) createOrRecycleView(mContext);
        photoView.setImageBitmap(imageBitmapAtPosition(position));
        container.addView(photoView, 0);
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
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            photoView.setMinimumHeight(FodexImageContract.preferredMinimumHeight(mContext));
        } else {
            photoView = mRecycledViews.pop();
        }
        return photoView;
    }

    private void cleanView(TouchImageView v) {
        v.resetZoom();
    }
}
