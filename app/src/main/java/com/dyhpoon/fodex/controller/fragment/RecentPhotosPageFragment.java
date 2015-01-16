package com.dyhpoon.fodex.controller.fragment;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.dyhpoon.fodex.model.MediaPhotoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darrenpoon on 8/12/14.
 */
public class RecentPhotosPageFragment extends FodexBaseFragment {

    @Override
    protected void onClickFloatingActionButton() {
        // TODO: implement
    }

    @Override
    protected Uri imageUriForItems(int position, MediaPhotoItem item) {
        return ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                Integer.parseInt(item.getId()));
    }

    @Override
    protected List<MediaPhotoItem> itemsForAdapters() {
        ContentResolver resolver = getActivity().getContentResolver();
        String[] projection = new String[]{
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
        return items;
    }

}
