package com.dyhpoon.fodex.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.dyhpoon.fodex.util.OnCompleteListener;

/**
 * Created by darrenpoon on 20/3/15.
 */
public class GoogleSharing extends Sharing {

    private static final String GOOGLE_PLUS_PACKAGE = "com.google.android.apps.plus";

    @Override
    public void shareImage(Context context, Uri uri, OnCompleteListener listener) {
        if (isPackageInstalled(GOOGLE_PLUS_PACKAGE, context)) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setPackage(GOOGLE_PLUS_PACKAGE);

            listener.didComplete();
            context.startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } else {
            listener.didFail();
        }
    }
}
