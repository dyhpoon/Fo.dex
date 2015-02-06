package com.dyhpoon.fodex.view;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.dyhpoon.fodex.data.FodexImageContract;

/**
 * Created by darrenpoon on 9/12/14.
 */
public class ImageGridItem extends LinearLayout {

    private ImageView mImageView;

    public ImageGridItem(Context context) {
        super(context);

        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(
                FodexImageContract.LEFT_MARGIN,
                FodexImageContract.TOP_MARGIN,
                FodexImageContract.RIGHT_MARGIN,
                FodexImageContract.BOTTOM_MARGIN
        );
        cardView.setLayoutParams(params);
        cardView.setCardElevation(14.5f);
        this.addView(cardView);

        mImageView = new ImageView(context);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mImageView.setScaleType(FodexImageContract.PREFERRED_SCALE_TYPE);
        cardView.addView(mImageView);
    }

    public void loadImage(Uri uri, Activity activity) {
        Glide.with(activity).load(uri.toString()).into(mImageView);
    }

    public void loadImage(String url, Activity activity) {
        Glide.with(activity).load(url).into(mImageView);
    }

}
