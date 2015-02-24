package com.dyhpoon.fodex.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dyhpoon.fodex.R;

/**
 * Created by darrenpoon on 24/2/15.
 */
public class CustomToast extends Toast {

    protected CustomToast(Context context) {
        super(context);
    }

    protected static Toast make(Context context, CharSequence text, int backgroundColor, Drawable icon) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_custom, null);

        CardView cardView = (CardView) layout.findViewById(R.id.card_view);
        cardView.setCardBackgroundColor(backgroundColor);

        RobotoRegularTextView textView = (RobotoRegularTextView) layout.findViewById(R.id.text_view);
        textView.setText(text);

        ImageView imageView = (ImageView) layout.findViewById(R.id.image_view);
        imageView.setImageDrawable(icon);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        return toast;
    }

}
