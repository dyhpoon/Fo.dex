package com.dyhpoon.fodex.fullscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dyhpoon.fodex.R;

/**
 * Created by darrenpoon on 26/1/15.
 */
public abstract class ImageAdapter extends PagerAdapter {

    public abstract Bitmap imageBitmapAtPosition(int position);

    private Context mContext;

    public ImageAdapter(Context context) {
        mContext = context;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.fullscreen_padding);
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageBitmap(imageBitmapAtPosition(position));
        container.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}
