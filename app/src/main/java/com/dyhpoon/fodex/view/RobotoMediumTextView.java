package com.dyhpoon.fodex.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by darrenpoon on 19/1/15.
 */
public class RobotoMediumTextView extends TextView {

    public RobotoMediumTextView(Context context) {
        super(context);
        setup(context);
    }

    public RobotoMediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public RobotoMediumTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    private void setup(Context context) {
        Typeface robotoMedium = Typeface.createFromAsset(
                context.getAssets(),
                "fonts/Roboto-Medium.ttf");
        setTypeface(robotoMedium);
    }
}
