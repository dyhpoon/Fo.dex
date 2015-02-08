package com.dyhpoon.fodex.contentFragment;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.dyhpoon.fodex.data.FodexCursor;
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
        Cursor cursor = FodexCursor.allPhotosCursor(getActivity());

        List<PhotoMedia> items = new ArrayList<PhotoMedia>();
        if (cursor.moveToFirst()) {
            final int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            do {
                final String id = cursor.getString(idColumn);
                items.add(new PhotoMedia(id));
            } while (cursor.moveToNext());

        }
        cursor.close();
        return items;
    }

    public class PhotoMedia {
        public String id;

        public PhotoMedia(String id) {
            this.id = id;
        }
    }

}
