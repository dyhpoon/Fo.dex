package com.dyhpoon.fodex.navigationDrawer;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.controller.fragment.AllPhotosPageFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by darrenpoon on 21/1/15.
 */
public class NavigationDrawerData {

    private static List<DrawerInfo> mPageItems;
    private static List<DrawerInfo> mUtilityItems;

    public static List<DrawerInfo> getPageItems(Context context) {
        if (mPageItems == null) {
            mPageItems = Arrays.asList(
                    new DrawerInfo(
                            context.getString(R.string.title_all_photos),
                            context.getResources().getDrawable(R.drawable.ic_all),
                            AllPhotosPageFragment.class),
                    new DrawerInfo(
                            context.getString(R.string.title_recent_photos),
                            context.getResources().getDrawable(R.drawable.ic_clock),
                            AllPhotosPageFragment.class),
                    new DrawerInfo(
                            context.getString(R.string.title_indexed_photos),
                            context.getResources().getDrawable(R.drawable.ic_indexed),
                            AllPhotosPageFragment.class),
                    new DrawerInfo(
                            context.getString(R.string.title_unindexed_photos),
                            context.getResources().getDrawable(R.drawable.ic_unindexed),
                            AllPhotosPageFragment.class)
            );
        }
        return mPageItems;
    }

    public static List<DrawerInfo> getUtilityItems(Context context) {
        if (mUtilityItems == null) {
            mUtilityItems = Arrays.asList(
                    new DrawerInfo(
                            context.getString(R.string.title_settings),
                            context.getResources().getDrawable(R.drawable.ic_settings),
                            AllPhotosPageFragment.class)
            );
        }
        return mUtilityItems;
    }

    public static DrawerInfo getPageItem(Context context, int position) {
        return getPageItems(context).get(position);
    }

    public static DrawerInfo getUtilityItem(Context context, int position) {
        return getUtilityItems(context).get(position);
    }

    public static class DrawerInfo {
        public String title;
        public Drawable drawable;
        public Class classType;

        public DrawerInfo(String title, Drawable drawable, Class classType) {
            this.title = title;
            this.drawable = drawable;
            this.classType = classType;
        }
    }

}
