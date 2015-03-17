package com.dyhpoon.fodex.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Toast;

import com.dyhpoon.fodex.R;

/**
 * Created by darrenpoon on 24/2/15.
 */
public class PleaseInertTagToast extends Toast {

    private PleaseInertTagToast(Context context) {
        super(context);
    }

    @TargetApi(21)
    public static Toast make(Context context) {
        Resources res = context.getResources();
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = res.getDrawable(R.drawable.ic_cross_fit_15, null);
        } else {
            drawable = res.getDrawable(R.drawable.ic_cross_fit_15);
        }
        return CustomToast.make(
                context,
                res.getString(R.string.message_please_insert_tag),
                res.getColor(R.color.red),
                drawable);
    }
}
