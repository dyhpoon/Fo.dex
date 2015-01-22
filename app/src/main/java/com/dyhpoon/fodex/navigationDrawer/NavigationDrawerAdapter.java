package com.dyhpoon.fodex.navigationDrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.dyhpoon.fodex.R;

/**
 * Created by darrenpoon on 21/1/15.
 */
public class NavigationDrawerAdapter extends BaseAdapter {

    private Context mContext;

    private static final int TYPE_LIST_ITEM = 0;
    private static final int TYPE_LIST_DIVIDER = 1;

    private enum ViewType {
        PAGE, UTILITY, DIVIDER
    }

    public NavigationDrawerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return NavigationDrawerData.getPageItems(mContext).size()
                + NavigationDrawerData.getUtilityItems(mContext).size()
                + 1;
    }

    @Override
    public int getItemViewType(int position) {
        int pageCount = NavigationDrawerData.getPageItems(mContext).size();
        return (position == pageCount)
                ? TYPE_LIST_DIVIDER
                : TYPE_LIST_ITEM;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        int pageCount = NavigationDrawerData.getPageItems(mContext).size();
        return (position == pageCount)
                ? false
                : true;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int pageCount = NavigationDrawerData.getPageItems(mContext).size();
        int utilityCount = NavigationDrawerData.getUtilityItems(mContext).size();

        // find the type of view
        ViewType type;
        if (position < pageCount) type = ViewType.PAGE;
        else if (position == pageCount) type = ViewType.DIVIDER;
        else if (position - pageCount - 1 < utilityCount) type = ViewType.UTILITY;
        else throw new RuntimeException("Unhandled Navigation drawer view type");

        switch (type) {
            // Page items (ex: All Photos, Recent Photos ...)
            case PAGE: {
                if (convertView == null) convertView = inflateListItem();

                NavigationDrawerItem navItem = (NavigationDrawerItem)convertView;
                NavigationDrawerInfo info =
                        NavigationDrawerData.getPageItem(mContext, position);
                navItem.iconImageView.setImageDrawable(info.drawable);
                navItem.titleTextView.setText(info.title);
                break;
            }

            // Divider
            case UTILITY: {
                if (convertView == null) convertView = inflateListItem();

                NavigationDrawerItem navItem = (NavigationDrawerItem)convertView;
                NavigationDrawerInfo info =
                        NavigationDrawerData.getUtilityItem(mContext, position - pageCount - 1);
                navItem.iconImageView.setImageDrawable(info.drawable);
                navItem.titleTextView.setText(info.title);
                break;
            }

            // Utility items (ex: Settings)
            case DIVIDER: {
                if (convertView == null) convertView = inflateListDivider();
                break;
            }
        }

        return convertView;
    }

    private View inflateListItem() {
        NavigationDrawerItem view = new NavigationDrawerItem(mContext);
        return view;
    }

    private View inflateListDivider() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.list_item_divider, null);
    }

}
