package com.dyhpoon.fodex.view;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import com.dyhpoon.fodex.R;

/**
 * Created by darrenpoon on 24/2/15.
 */
public class NoPhotoToast extends CustomToast {

    private NoPhotoToast(Context context) {
        super(context);
    }

    public static Toast make(Context context) {
        Resources res = context.getResources();
        return CustomToast.make(
                context,
                res.getString(R.string.message_no_photo),
                res.getColor(R.color.red),
                res.getDrawable(R.drawable.ic_cross_fit_15));
    }

}
