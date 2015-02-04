package com.dyhpoon.fodex.data;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.widget.ImageView;

/**
 * Created by darrenpoon on 4/2/15.
 */
public class FodexImageContract {

    public static int LEFT_MARGIN    = 5;
    public static int TOP_MARGIN     = 5;
    public static int RIGHT_MARGIN   = 5;
    public static int BOTTOM_MARGIN  = 5;

    public static ImageView.ScaleType PREFERRED_SCALE_TYPE = ImageView.ScaleType.CENTER_CROP;

    public static int preferredMinimumHeight(Context context) {
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return (int) (size.y * 0.4);
    }

}
