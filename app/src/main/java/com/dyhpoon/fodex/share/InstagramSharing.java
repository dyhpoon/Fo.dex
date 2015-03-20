package com.dyhpoon.fodex.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.dyhpoon.fodex.util.OnCompleteListener;

/**
 * Created by darrenpoon on 20/3/15.
 */
public class InstagramSharing extends Sharing {

    private static final String INSTAGRAM_PACKAGE = "com.instagram.android";

    @Override
    public void shareImage(Context context, Uri uri, OnCompleteListener listener) {
        if (isPackageInstalled(INSTAGRAM_PACKAGE, context)) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.setPackage(INSTAGRAM_PACKAGE);
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            listener.didComplete();
            context.startActivity(Intent.createChooser(share, "Share to"));
        } else {
            listener.didFail();
        }
    }
}
