package com.dyhpoon.fodex.navigationDrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dyhpoon.fodex.R;

/**
 * Created by darrenpoon on 21/1/15.
 */
public class NavigationDrawerAdapter extends BaseAdapter {

    private Context mContext;

    public NavigationDrawerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return NavigationDrawerData.getPageItems(mContext).size()
                + NavigationDrawerData.getUtilityItems(mContext).size();
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
        View view;
        if (convertView == null) {
            // inflate layout
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_navigation_drawer, null);

            // setup view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.iconImageView = (ImageView) view.findViewById(R.id.icon);
            viewHolder.titleTextView = (TextView) view.findViewById(R.id.title);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int pageCount = NavigationDrawerData.getPageItems(mContext).size();
        int utilityCount = NavigationDrawerData.getUtilityItems(mContext).size();

        // Page items (ex: All Photos, Recent Photos ...)
        if (position < pageCount) {
            NavigationDrawerInfo info =
                    NavigationDrawerData.getPageItem(mContext, position);
            viewHolder.iconImageView.setImageDrawable(info.drawable);
            viewHolder.titleTextView.setText(info.title);
        }
        // Utility items (ex: Settings)
        else if (position - pageCount < utilityCount) {
            NavigationDrawerInfo info =
                    NavigationDrawerData.getUtilityItem(mContext, position - pageCount);
            viewHolder.iconImageView.setImageDrawable(info.drawable);
            viewHolder.titleTextView.setText(info.title);
        }
        else {
            throw new RuntimeException("Undefined DrawerInfo");
        }

        return view;
    }

    private class ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;
    }

}
