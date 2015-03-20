package com.dyhpoon.fodex.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Toast;

import com.dyhpoon.fodex.R;

/**
 * Created by darrenpoon on 17/3/15.
 */
public class PleaseInstallToast extends Toast {

    private PleaseInstallToast(Context context) {
        super(context);
    }

    @TargetApi(21)
    public static Toast make(Context context, String text) {
        Resources res = context.getResources();
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = res.getDrawable(R.drawable.ic_cross_fit_15, null);
        } else {
            drawable = res.getDrawable(R.drawable.ic_cross_fit_15);
        }
        return CustomToast.make(
                context,
                text,
                res.getColor(R.color.red),
                drawable);
    }
}
