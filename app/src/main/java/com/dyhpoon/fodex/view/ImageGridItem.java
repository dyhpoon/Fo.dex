package com.dyhpoon.fodex.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.data.actual.FodexImageContract;

import java.util.Random;

/**
 * Created by darrenpoon on 9/12/14.
 */
public class ImageGridItem extends LinearLayout {

    public ImageView imageView;
    public ColorDrawable colorDrawable = generateColorDrawable();

    private ImageView mSelectedImageView;

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

        imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(FodexImageContract.PREFERRED_SCALE_TYPE);
        cardView.addView(imageView);

        mSelectedImageView = new ImageView(context);
        mSelectedImageView.setBackgroundColor(Color.BLACK);
        mSelectedImageView.setScaleType(ImageView.ScaleType.CENTER);
        mSelectedImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_selected));
        mSelectedImageView.setAlpha(0.8f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        mSelectedImageView.setLayoutParams(lp);
        mSelectedImageView.setLayerType(LAYER_TYPE_HARDWARE, null);
        mSelectedImageView.setVisibility(INVISIBLE);
        cardView.addView(mSelectedImageView);
    }

    @Override
    public void setSelected(boolean isSelected) {
        if (isSelected != isSelected()) {
            super.setSelected(isSelected);
            if (isSelected) {
                mSelectedImageView.setVisibility(VISIBLE);

            } else {
                mSelectedImageView.setVisibility(INVISIBLE);
            }
        }
    }

    private ColorDrawable generateColorDrawable() {
        final int max = 250;
        final int min = 120;
        Random rnd = new Random();

        int randomR = rnd.nextInt((max - min) + 1) + min;
        int randomG = rnd.nextInt((max - min) + 1) + min;
        int randomB = rnd.nextInt((max - min) + 1) + min;

        return new ColorDrawable(Color.argb(255, randomR, randomG, randomB));
    }

}
