package com.dyhpoon.fodex.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.util.OnCompleteListener;
import com.google.android.gms.plus.PlusShare;

/**
 * Created by darrenpoon on 20/3/15.
 */
public class GoogleSharing extends Sharing {

    @Override
    public void shareImage(Context context, Uri uri, OnCompleteListener listener) {
        String mime = context.getContentResolver().getType(uri);
        Intent shareIntent = new PlusShare.Builder(context)
                .addStream(uri)
                .setType(mime)
                .getIntent();
        listener.didComplete();
        context.startActivity(shareIntent);
    }
}
