package com.dyhpoon.fodex.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.util.OnCompleteListener;

/**
 * Created by darrenpoon on 20/3/15.
 */
public class FacebookSharing extends Sharing {

    private static final String FACEBOOK_PACKAGE = "com.facebook.katana";

    @Override
    public void shareImage(Context context, Uri uri, OnCompleteListener listener) {
        if (isPackageInstalled(FACEBOOK_PACKAGE, context)) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.setPackage(FACEBOOK_PACKAGE);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(
                    "com.facebook.platform.extra.APPLICATION_ID",
                    context.getResources().getString(R.string.facebook_app_id));

            listener.didComplete();
            context.startActivity(Intent.createChooser(shareIntent, "Share"));
        } else {
            listener.didFail();
        }
    }
}
