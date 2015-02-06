package com.dyhpoon.fodex.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dyhpoon.fodex.data.FodexImageContract;

import java.util.Random;

/**
 * Created by darrenpoon on 9/12/14.
 */
public class ImageGridItem extends LinearLayout {

    public ImageView imageView;

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
        cardView.setBackgroundColor(randomColor());
    }

    private int randomColor() {
        final int max = 255;
        final int min = 150;
        Random rnd = new Random();

        int randomR = rnd.nextInt((max - min) + 1) + min;
        int randomG = rnd.nextInt((max - min) + 1) + min;
        int randomB = rnd.nextInt((max - min) + 1) + min;

        return Color.argb(255, randomR, randomG, randomB);
    }

}
