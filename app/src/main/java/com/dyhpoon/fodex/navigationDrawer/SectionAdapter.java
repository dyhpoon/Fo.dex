package com.dyhpoon.fodex.navigationDrawer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by darrenpoon on 3/2/15.
 */
public abstract class SectionAdapter extends BaseAdapter {

    public abstract int getSectionCount();

    public abstract int getViewCountAtSection(int section);

    public abstract View getView(int section, int position, View convertView, ViewGroup parent);

    public abstract Boolean isSectionEnabled(int section);

    public int getCorrespondingPosition(int position, int section) {
        int offset = 0;
        for (int i = 0; i < section; i++) {
            offset += getViewCountAtSection(i);
        }
        return position - offset;
    }

    @Override
    public boolean isEnabled(int position) {
        int section = getItemViewType(position);
        return isSectionEnabled(section);
    }

    @Override
    public int getViewTypeCount() {
        return getSectionCount();
    }

    @Override
    public int getItemViewType(int position) {
        int i, offset = 0;
        for (i = 0; i < getSectionCount() ; i++) {
            offset += getViewCountAtSection(i);
            if (position < offset) break;
        }
        return i;
    }

    @Override
    public int getCount() {
        int count = 0;
        for (int i = 0; i < getSectionCount(); i++) {
            count += getViewCountAtSection(i);
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int section = getItemViewType(position);
        int correspondingPosition = getCorrespondingPosition(position, section);
        return getView(section, correspondingPosition, convertView, parent);
    }
}
