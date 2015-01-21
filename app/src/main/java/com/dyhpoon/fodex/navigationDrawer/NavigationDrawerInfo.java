package com.dyhpoon.fodex.navigationDrawer;

import android.graphics.drawable.Drawable;

/**
 * Created by darrenpoon on 21/1/15.
 */
public class NavigationDrawerInfo {
    public String tag;
    public String title;
    public Drawable drawable;
    public Class classType;

    public NavigationDrawerInfo(String tag, String title, Drawable drawable, Class classType) {
        this.tag = tag;
        this.title = title;
        this.drawable = drawable;
        this.classType = classType;
    }
}

