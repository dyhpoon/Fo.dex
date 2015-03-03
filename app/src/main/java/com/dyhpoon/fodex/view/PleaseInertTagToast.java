package com.dyhpoon.fodex.view;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import com.dyhpoon.fodex.R;

/**
 * Created by darrenpoon on 24/2/15.
 */
public class PleaseInertTagToast extends Toast {

    private PleaseInertTagToast(Context context) {
        super(context);
    }

    public static Toast make(Context context) {
        Resources res = context.getResources();
        return CustomToast.make(
                context,
                res.getString(R.string.message_please_insert_tag),
                res.getColor(R.color.red),
                res.getDrawable(R.drawable.ic_cross_fit_15));
    }
}
