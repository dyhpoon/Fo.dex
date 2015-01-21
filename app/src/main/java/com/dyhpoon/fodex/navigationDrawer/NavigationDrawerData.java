package com.dyhpoon.fodex.navigationDrawer;

import android.content.Context;

import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.controller.fragment.AllPhotosPageFragment;

import java.util.Arrays;
import java.util.List;

/**
 * Created by darrenpoon on 21/1/15.
 */
public class NavigationDrawerData {

    private static List<NavigationDrawerInfo> mPageItems;
    private static List<NavigationDrawerInfo> mUtilityItems;

    private static final String PREFIX = NavigationDrawerData.class.getName() + "_";
    public static final String TAG_ALL_PHOTOS       = PREFIX + "ALL_PHOTOS";
    public static final String TAG_RECENT_PHOTOS    = PREFIX + "RECENT_PHOTOS";
    public static final String TAG_INDEXED_PHOTOS   = PREFIX + "INDEXED_PHOTOS";
    public static final String TAG_UNINDEXED_PHOTOS = PREFIX + "UNINDEXED_PHOTOS";
    public static final String TAG_SETTINGS         = PREFIX + "SETTINGS";

    public static List<NavigationDrawerInfo> getPageItems(Context context) {
        if (mPageItems == null) {
            mPageItems = Arrays.asList(
                    new NavigationDrawerInfo(
                            TAG_ALL_PHOTOS,
                            context.getString(R.string.title_all_photos),
                            context.getResources().getDrawable(R.drawable.ic_all),
                            AllPhotosPageFragment.class),
                    new NavigationDrawerInfo(
                            TAG_RECENT_PHOTOS,
                            context.getString(R.string.title_recent_photos),
                            context.getResources().getDrawable(R.drawable.ic_clock),
                            AllPhotosPageFragment.class),
                    new NavigationDrawerInfo(
                            TAG_INDEXED_PHOTOS,
                            context.getString(R.string.title_indexed_photos),
                            context.getResources().getDrawable(R.drawable.ic_indexed),
                            AllPhotosPageFragment.class),
                    new NavigationDrawerInfo(
                            TAG_UNINDEXED_PHOTOS,
                            context.getString(R.string.title_unindexed_photos),
                            context.getResources().getDrawable(R.drawable.ic_unindexed),
                            AllPhotosPageFragment.class)
            );
        }
        return mPageItems;
    }

    public static NavigationDrawerInfo getPageItem(Context context, int position) {
        return getPageItems(context).get(position);
    }

    public static List<NavigationDrawerInfo> getUtilityItems(Context context) {
        if (mUtilityItems == null) {
            mUtilityItems = Arrays.asList(
                    new NavigationDrawerInfo(
                            TAG_SETTINGS,
                            context.getString(R.string.title_settings),
                            context.getResources().getDrawable(R.drawable.ic_settings),
                            AllPhotosPageFragment.class)
            );
        }
        return mUtilityItems;
    }

    public static NavigationDrawerInfo getUtilityItem(Context context, int position) {
        return getUtilityItems(context).get(position);
    }

}
