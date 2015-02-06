package com.dyhpoon.fodex.contentFragment;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.dyhpoon.fodex.fodexView.FodexBaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darrenpoon on 8/12/14.
 */
public class RecentPhotosPageFragment extends FodexBaseFragment<RecentPhotosPageFragment.PhotoMedia> {

    @Override
    protected void onClickFloatingActionButton() {
        // TODO: implement
    }

    @Override
    protected Uri imageUriForItems(PhotoMedia item) {
        return ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                Integer.parseInt(item.id));
    }

    @Override
    protected List<PhotoMedia> itemsForAdapters() {
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

        List<PhotoMedia> items = new ArrayList<PhotoMedia>();
        if (cursor.moveToLast()) {
            final int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            final int dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);

            do {
                final String id = cursor.getString(idColumn);
                final String data = cursor.getString(dataColumn);
                final String date = cursor.getString(dateColumn);

                items.add(new PhotoMedia(id, data, date));
            } while (cursor.moveToPrevious());

        }
        cursor.close();
        return items;
    }

    public class PhotoMedia {
        public String id;
        public String data;
        public String date;

        public PhotoMedia(String id, String data, String date) {
            this.id = id;
            this.data = data;
            this.date = date;
        }
    }

}
