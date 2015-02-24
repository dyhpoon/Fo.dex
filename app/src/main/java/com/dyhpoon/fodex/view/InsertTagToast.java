package com.dyhpoon.fodex.view;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import com.dyhpoon.fodex.R;

/**
 * Created by darrenpoon on 24/2/15.
 */
public class InsertTagToast extends CustomToast {

    private InsertTagToast(Context context) {
        super(context);
    }

    public static Toast make(Context context, int number) {
        Resources res = context.getResources();
        return CustomToast.make(
                context,
                res.getQuantityString(R.plurals.message_add_tag, number, number),
                res.getColor(R.color.colorPrimaryDark),
                res.getDrawable(R.drawable.ic_tag_fit_15));
    }
}
