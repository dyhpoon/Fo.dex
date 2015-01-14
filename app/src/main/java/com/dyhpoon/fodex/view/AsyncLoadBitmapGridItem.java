package com.dyhpoon.fodex.view;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
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

        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(5, 5, 5, 5);
        cardView.setLayoutParams(params);
        cardView.setCardElevation(14.5f);
        this.addView(cardView);

        mImageView = new ImageView(context);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        cardView.addView(mImageView);
    }

    public void loadImage(Uri uri) {
        mImageLoader.displayImage(uri.toString(), mImageView);
    }

}
