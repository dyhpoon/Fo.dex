package com.dyhpoon.fodex.navigationDrawer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.dyhpoon.fodex.R;

/**
 * Created by darrenpoon on 3/2/15.
 */
public class NavigationDrawerAdapter extends SectionAdapter {

    private Context mContext;

    public class ViewType {
        public static final int PROFILE     = 0;
        public static final int WHITESPACE  = 1;
        public static final int PAGE        = 2;
        public static final int DIVIDER     = 3;
        public static final int UTILITY     = 4;
    }

    public NavigationDrawerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getSectionCount() {
        return 5;   // all view types
    }

    @Override
    public Boolean isSectionEnabled(int section) {
        switch (section) {
            case ViewType.PROFILE:
                return false;
            case ViewType.WHITESPACE:
                return false;
            case ViewType.PAGE:
                return true;
            case ViewType.DIVIDER:
                return false;
            case ViewType.UTILITY:
                return true;
            default:
                throw new IllegalArgumentException(
                        "Unhandled section number in navigation drawer adapter, found: " + section);
        }
    }

    @Override
    public int getViewCountAtSection(int section) {
        switch (section) {
            case ViewType.PROFILE:
                return 1;
            case ViewType.WHITESPACE:
                return 1;
            case ViewType.PAGE:
                return NavigationDrawerData.getPageItems(mContext).size();
            case ViewType.DIVIDER:
                return 1;
            case ViewType.UTILITY:
                return NavigationDrawerData.getUtilityItems(mContext).size();
            default:
                throw new IllegalArgumentException(
                        "Unhandled section number in navigation drawer adapter, found: " + section);
        }
    }

    @Override
    public View getView(int section, int position, View convertView, ViewGroup parent) {
        switch (section) {
            case 0:
                convertView = inflateProfile(convertView);
                break;
            case 1:
                convertView = inflateWhiteSpace(convertView);
                break;
            case 2:
                convertView = inflatePageItem(convertView, position);
                break;
            case 3:
                convertView = inflateListDivider(convertView);
                break;
            case 4:
                convertView = inflateUtilityItem(convertView, position);
                break;
            default:
                throw new IllegalArgumentException(
                        "Unhandled section/position number in navigation drawer adapter, " +
                                "found section: " + section + " position: " + position);
        }
        return convertView;
    }

    private View inflateProfile(View convertView) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item_profile, null);
        }
        return convertView;
    }

    private View inflateWhiteSpace(View convertView) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item_whitespace, null);
        }
        return convertView;
    }

    private View inflatePageItem(View convertView, int position) {
        if (convertView == null) {
            convertView = new NavigationDrawerItem(mContext);
        }
        NavigationDrawerInfo info = NavigationDrawerData.getPageItem(mContext, position);
        ((NavigationDrawerItem)convertView).iconImageView.setImageDrawable(info.drawable);
        ((NavigationDrawerItem)convertView).titleTextView.setText(info.title);
        return convertView;
    }

    private View inflateUtilityItem(View convertView, int position) {
        if (convertView == null) {
            convertView = new NavigationDrawerItem(mContext);
        }
        NavigationDrawerInfo info = NavigationDrawerData.getUtilityItem(mContext, position);
        ((NavigationDrawerItem)convertView).iconImageView.setImageDrawable(info.drawable);
        ((NavigationDrawerItem)convertView).titleTextView.setText(info.title);
        return convertView;
    }

    private View inflateListDivider(View convertView) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item_divider, null);
        }
        return convertView;
    }
}
