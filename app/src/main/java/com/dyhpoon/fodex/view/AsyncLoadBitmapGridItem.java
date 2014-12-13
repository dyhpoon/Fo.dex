package com.dyhpoon.fodex.view;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by darrenpoon on 9/12/14.
 */
public class AsyncLoadBitmapGridItem extends LinearLayout {

    private ImageView mImageView;
    private ImageLoader mImageLoader;

    public AsyncLoadBitmapGridItem(Context context) {
        super(context);

        mImageLoader = ImageLoader.getInstance();

        mImageView = new ImageView(context);
        mImageView.setBackgroundColor(Color.GREEN);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.addView(mImageView);
    }

    public void loadImage(Uri uri) {
        mImageLoader.displayImage(uri.toString(), mImageView);
    }


}
