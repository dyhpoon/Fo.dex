package com.dyhpoon.fodex.data;

import android.content.Context;
import android.database.Cursor;

import java.util.List;

/**
 * Created by darrenpoon on 28/3/15.
 */
public interface FodexCore {

    public List<FodexItem> getAllPhotoItems(Context context);

    public List<FodexItem> getIndexedPhotoItems(Context context);

    public List<FodexItem> getUnindexPhotoItems(Context context);

    public List<FodexItem> getSharedPhotoItems(Context context);

    public List<FodexItem> getSearchedPhotoItems(Context context, List<String> tagNames);

    public List<String> getTags(Context context, long imageId);

    public Cursor getMatchedTags(Context context, String words);

    public void addTagsToPhotos(Context context, long[] imageIds, String[] tags);

    public void addSharePhotos(Context context, long[] imageIds);

    public void deleteTagFromPhoto(Context context, long imageId, String tag);

    public long getTagId(Context context, String tag);

    public void syncAllPhotos(final Context context);

}
