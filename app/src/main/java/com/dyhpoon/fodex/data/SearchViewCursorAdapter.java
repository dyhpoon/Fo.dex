package com.dyhpoon.fodex.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dyhpoon.fodex.R;

/**
 * Created by darrenpoon on 8/3/15.
 */
public class SearchViewCursorAdapter extends CursorAdapter {

    public SearchViewCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public SearchViewCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
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
        holder.textview.setText(cursor.getString(cursor.getColumnIndex(FodexContract.TagEntry.COLUMN_TAG_NAME)));
    }

    private class SearchViewHolder {
        public TextView textview;

        public SearchViewHolder(View view) {
            textview = (TextView) view.findViewById(R.id.text_view);
        }
    }
}
