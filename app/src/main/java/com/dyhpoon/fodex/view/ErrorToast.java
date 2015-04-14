package com.dyhpoon.fodex.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.dyhpoon.fodex.R;

/**
 * Created by darrenpoon on 3/3/15.
 */
public class ErrorToast extends CustomToast {

    private ErrorToast(Context context) {
        super(context);
    }

    public static Toast make(Context context, String message) {
        Resources res = context.getResources();
        return CustomToast.make(
                context,
                message,
                res.getColor(R.color.red),
                ContextCompat.getDrawable(context, R.drawable.ic_cross_fit_15));
    }
}
