package com.dyhpoon.fodex.share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by darrenpoon on 16/3/15.
 */
public abstract class Sharing {

    public abstract void shareImage(Context context, Uri uri);

    public boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
