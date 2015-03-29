package com.dyhpoon.fodex.fodexView;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.data.actual.FodexContract;

/**
 * Created by darrenpoon on 8/3/15.
 */
public class SearchViewCursorAdapter extends CursorAdapter {

    private String mAppendedString;

    public SearchViewCursorAdapter(Context context, Cursor c, String appendedString) {
        super(context, c, true);
        mAppendedString = appendedString;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_search, parent, false);
        view.setTag(new SearchViewHolder(view));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        SearchViewHolder holder = (SearchViewHolder) view.getTag();
        String suggestion = mAppendedString + " " + cursor.getString(cursor.getColumnIndex(FodexContract.TagEntry.COLUMN_TAG_NAME));
        holder.textview.setText(suggestion);
    }

    private class SearchViewHolder {
        public TextView textview;

        public SearchViewHolder(View view) {
            textview = (TextView) view.findViewById(R.id.text_view);
        }
    }
}
