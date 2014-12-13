package com.dyhpoon.fodex;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dyhpoon.fodex.model.MediaPhotoItem;
import com.dyhpoon.fodex.view.AsyncLoadBitmapGridItem;
import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;
import com.felipecsl.asymmetricgridview.library.widget.RowInfo;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darrenpoon on 8/12/14.
 */
public class RecentPhotosPageFragment extends Fragment {

    private AsymmetricGridView mGridView;

    private int THREAD_POOL_SIZE = 3;
    private int PRELOAD_IMAGE_COUNT = 10;
    private List<AsyncTask> mBackgroundTasks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_recent_photos, container, false);

        mGridView = (AsymmetricGridView) view.findViewById(R.id.gridView);
        mGridView.setRequestedColumnCount(3);
        mGridView.setRequestedHorizontalSpacing(Utils.dpToPx(getActivity(), 3));
        mGridView.setAdapter(new AsymmetricGridViewAdapter<MediaPhotoItem>(getActivity(), mGridView, new ArrayList<MediaPhotoItem>()) {
            @Override
            public View getActualView(int position, View convertView, ViewGroup parent) {
                AsyncLoadBitmapGridItem gridItem;
                MediaPhotoItem item = getItem(position);
                if (convertView == null) {
                    gridItem = new AsyncLoadBitmapGridItem(getActivity());
                } else {
                    gridItem = (AsyncLoadBitmapGridItem) convertView;
                }
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.parseInt(item.getId()));
                gridItem.loadImage(uri);
                return gridItem;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (getBackgroundTasks().size() >= THREAD_POOL_SIZE) {
                    AsyncTask task = getBackgroundTasks().get(0);
                    task.cancel(true);
                    getBackgroundTasks().remove(0);
                }
                getBackgroundTasks().add(new PreloadImageTask().execute());
                return view;
            }

            class PreloadImageTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... params) {
                    // preload images above the screen
                    RowInfo<MediaPhotoItem> firstRowInfo = getRowInfo(mGridView.getFirstVisiblePosition());
                    if (firstRowInfo != null) {
                        List<MediaPhotoItem> firstRowItems = firstRowInfo.getItems();
                        MediaPhotoItem firstItem = firstRowItems.get(0);
                        int firstIndex = getIndexOfItem(firstItem);
                        for (int i = 1; i < PRELOAD_IMAGE_COUNT; i++) {
                            int index = firstIndex - i;
                            MediaPhotoItem previousItem = getItem(index);
                            if (previousItem != null)
                                previousItem.preloadImage(new ImageSize(getRowWidth(previousItem), getRowHeight(previousItem)));
                        }
                    }

                    // preload images below the screen
                    RowInfo<MediaPhotoItem> lastRowInfo = getRowInfo(mGridView.getLastVisiblePosition());
                    if (lastRowInfo != null) {
                        List<MediaPhotoItem> lastRowItems = lastRowInfo.getItems();
                        MediaPhotoItem lastItem = lastRowItems.get(lastRowItems.size() - 1);
                        int lastIndex = getIndexOfItem(lastItem);
                        for (int i = 1; i < PRELOAD_IMAGE_COUNT; i++) {
                            int index = lastIndex + i;
                            MediaPhotoItem preloadItem = getItem(index);
                            if (preloadItem != null) {
                                preloadItem.preloadImage(new ImageSize(getRowWidth(preloadItem), getRowHeight(preloadItem)));
                            }
                        }
                    }

                    return null;
                }
            }
        });

        return view;
    }

    private List<AsyncTask> getBackgroundTasks() {
        if (mBackgroundTasks == null)
            mBackgroundTasks = new ArrayList<AsyncTask>(THREAD_POOL_SIZE);
        return mBackgroundTasks;
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
