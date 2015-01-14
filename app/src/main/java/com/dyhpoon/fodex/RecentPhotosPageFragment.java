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
import android.widget.AbsListView;

import com.dyhpoon.fodex.controller.GridViewPreloadImageTask;
import com.dyhpoon.fodex.model.MediaPhotoItem;
import com.dyhpoon.fodex.model.ScrollDirectionType;
import com.dyhpoon.fodex.view.AsyncLoadBitmapGridItem;
import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darrenpoon on 8/12/14.
 */
public class RecentPhotosPageFragment extends Fragment {

    private AsymmetricGridView mGridView;

    private int mLastFirstVisibleItem = 0;
    private ScrollDirectionType mScrollDirection = ScrollDirectionType.DOWN;

    final private static int THREAD_POOL_SIZE = 3;
    private List<AsyncTask> mBackgroundTasks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_recent_photos, container, false);

        mGridView = (AsymmetricGridView) view.findViewById(R.id.gridView);
        mGridView.setRequestedColumnCount(3);
        mGridView.setRequestedHorizontalSpacing(Utils.dpToPx(getActivity(), 2));
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // do nothing
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view == mGridView) {
                    final int currentFirstVisibleItem = mGridView.getFirstVisiblePosition();

                    ScrollDirectionType direction = ScrollDirectionType.NONE;
                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        direction = ScrollDirectionType.DOWN;
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        direction = ScrollDirectionType.UP;
                    }

                    if (mScrollDirection != direction && direction != ScrollDirectionType.NONE) {
                        cancelPreloadImages();
                        mScrollDirection = direction;
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            }
        });
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
                preloadImages(mScrollDirection);
                return view;
            }
        });

        return view;
    }

    private void preloadImages(ScrollDirectionType scrollDirection) {
        if (mBackgroundTasks == null)
            mBackgroundTasks = new ArrayList<AsyncTask>(THREAD_POOL_SIZE);

        if (mBackgroundTasks.size() >= THREAD_POOL_SIZE) {
            AsyncTask task = mBackgroundTasks.get(0);
            task.cancel(true);
            mBackgroundTasks.remove(0);
        }
        mBackgroundTasks.add(new GridViewPreloadImageTask(mGridView).execute(scrollDirection));
    }

    private void cancelPreloadImages() {
        if (mBackgroundTasks == null)
            mBackgroundTasks = new ArrayList<AsyncTask>(THREAD_POOL_SIZE);

        for (AsyncTask task : mBackgroundTasks) {
            task.cancel(true);
        }
        mBackgroundTasks.clear();
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
        preloadImages(ScrollDirectionType.DOWN);
    }
}
