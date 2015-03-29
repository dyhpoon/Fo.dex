package com.dyhpoon.fodex.data.mock;

import android.content.Context;
import android.database.Cursor;

import com.dyhpoon.fodex.data.FodexCore;
import com.dyhpoon.fodex.data.FodexItem;

import java.util.List;

/**
 * Created by darrenpoon on 28/3/15.
 */
public class FodexCoreMockImpl implements FodexCore {

    @Override
    public List<FodexItem> getAllPhotoItems(Context context) {
        return null;
    }

    @Override
    public List<FodexItem> getIndexedPhotoItems(Context context) {
        return null;
    }

    @Override
    public List<FodexItem> getUnindexPhotoItems(Context context) {
        return null;
    }

    @Override
    public List<FodexItem> getSharedPhotoItems(Context context) {
        return null;
    }

    @Override
    public List<FodexItem> getSearchedPhotoItems(Context context, List<String> tagNames) {
        return null;
    }

    @Override
    public List<String> getTags(Context context, long imageId) {
        return null;
    }

    @Override
    public Cursor getMatchedTags(Context context, String words) {
        return null;
    }

    @Override
    public void addTagsToPhotos(Context context, long[] imageIds, String[] tags) {

    }

    @Override
    public void addSharePhotos(Context context, long[] imageIds) {

    }

    @Override
    public void deleteTagFromPhoto(Context context, long imageId, String tag) {

    }

    @Override
    public long getTagId(Context context, String tag) {
        return 0;
    }

    @Override
    public void syncAllPhotos(Context context) {

    }
}
