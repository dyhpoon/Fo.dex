package com.dyhpoon.fodex.controller.fragment;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.model.MediaPhotoItem;
import com.dyhpoon.fodex.view.ImageGridItem;
import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darrenpoon on 8/12/14.
 */
public class RecentPhotosPageFragment extends Fragment {

    private AsymmetricGridView mGridView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_recent_photos, container, false);


        mGridView = (AsymmetricGridView) view.findViewById(R.id.grid_view);
        FloatingActionButton floatingButton = (FloatingActionButton) view.findViewById(R.id.floating_button);

        mGridView.setRequestedColumnCount(3);
        mGridView.setRequestedHorizontalSpacing(Utils.dpToPx(getActivity(), 2));
        mGridView.setAdapter(new AsymmetricGridViewAdapter<MediaPhotoItem>(getActivity(), mGridView, new ArrayList<MediaPhotoItem>()) {
            @Override
            public View getActualView(int position, View convertView, ViewGroup parent) {
                ImageGridItem gridItem;
                MediaPhotoItem item = getItem(position);
                if (convertView == null) {
                    gridItem = new ImageGridItem(getActivity());
                } else {
                    gridItem = (ImageGridItem) convertView;
                }
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.parseInt(item.getId()));
                gridItem.loadImage(uri);
                return gridItem;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                return view;
            }
        });

        floatingButton.attachToListView(mGridView);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        return view;
    }

    public void update() {
        ContentResolver resolver = getActivity().getContentResolver();
        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
        };
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        List<MediaPhotoItem> items = new ArrayList<MediaPhotoItem>();
        if (cursor.moveToLast()) {
            final int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            final int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);

            do {
                final String id = cursor.getString(idColumn);
                final String data = cursor.getString(dataColumn);
                final String date = cursor.getString(dateColumn);

                int index = items.size() % 10;
                int columnSpan;
                switch (index) {
                    case 0:
                    case 6:
                        columnSpan = 2;
                        break;
                    default:
                        columnSpan = 1;
                        break;
                }
                items.add(new MediaPhotoItem(id, columnSpan, 1, data, date));
            } while (cursor.moveToPrevious());

        }
        cursor.close();
        ((AsymmetricGridViewAdapter) mGridView.getAdapter()).appendItems(items);
    }
}
