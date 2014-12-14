package com.dyhpoon.fodex.controller;

import android.os.AsyncTask;

import com.dyhpoon.fodex.model.MediaPhotoItem;
import com.dyhpoon.fodex.model.ScrollDirectionType;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;
import com.felipecsl.asymmetricgridview.library.widget.RowInfo;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.List;

/**
 * Created by darrenpoon on 14/12/14.
 */
public class GridViewPreloadImageTask extends AsyncTask<ScrollDirectionType, Void, Void> {

    AsymmetricGridView mGridView;
    AsymmetricGridViewAdapter mAdapter;

    private int PRELOAD_IMAGE_COUNT = 10;

    public GridViewPreloadImageTask(AsymmetricGridView gridView) {
        mGridView = gridView;
        mAdapter = (AsymmetricGridViewAdapter) mGridView.getAdapter();
    }

    @Override
    protected Void doInBackground(ScrollDirectionType... params) {
        ScrollDirectionType direction = params[0];

        // preload images above the screen
        if (direction == ScrollDirectionType.UP) {
            RowInfo<MediaPhotoItem> firstRowInfo = mAdapter.getRowInfo(mGridView.getFirstVisiblePosition());
            if (firstRowInfo != null) {
                List<MediaPhotoItem> firstRowItems = firstRowInfo.getItems();
                MediaPhotoItem firstItem = firstRowItems.get(0);
                int firstIndex = mAdapter.getIndexOfItem(firstItem);
                for (int i = 0; i < PRELOAD_IMAGE_COUNT; i++) {
                    int index = firstIndex - i;
                    MediaPhotoItem previousItem = (MediaPhotoItem) mAdapter.getItem(index);
                    if (previousItem != null)
                        previousItem.preloadImage(new ImageSize(mAdapter.getRowWidth(previousItem), mAdapter.getRowHeight(previousItem)));
                }
            }
        }

        // preload images below the screen
        else if (direction == ScrollDirectionType.DOWN) {
            RowInfo<MediaPhotoItem> lastRowInfo = mAdapter.getRowInfo(mGridView.getLastVisiblePosition());
            if (lastRowInfo != null) {
                List<MediaPhotoItem> lastRowItems = lastRowInfo.getItems();
                MediaPhotoItem lastItem = lastRowItems.get(lastRowItems.size() - 1);
                int lastIndex = mAdapter.getIndexOfItem(lastItem);
                for (int i = 0; i < PRELOAD_IMAGE_COUNT; i++) {
                    int index = lastIndex + i;
                    MediaPhotoItem preloadItem = (MediaPhotoItem) mAdapter.getItem(index);
                    if (preloadItem != null)
                        preloadItem.preloadImage(new ImageSize(mAdapter.getRowWidth(preloadItem), mAdapter.getRowHeight(preloadItem)));
                }
            }
        }

        return null;
    }
}