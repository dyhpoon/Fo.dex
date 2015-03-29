package com.dyhpoon.fodex.data.mock;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import com.dyhpoon.fodex.data.FodexCore;
import com.dyhpoon.fodex.data.FodexItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by darrenpoon on 28/3/15.
 * All items with even number ID will be tagged.
 * All items with odd number ID will not be tagged.
 * Item's url will be http://placekitten.com/{250+id}/250
 */
public class FodexCoreMockImpl implements FodexCore {

    @Override
    public List<FodexItem> getAllPhotoItems(Context context) {
        return generateFakeItems();
    }

    @Override
    public List<FodexItem> getIndexedPhotoItems(Context context) {
        return generateFilteredFakeItems(true);
    }

    @Override
    public List<FodexItem> getUnindexPhotoItems(Context context) {
        return generateFilteredFakeItems(false);
    }

    @Override
    public List<FodexItem> getSharedPhotoItems(Context context) {
        return generateFakeItems();
    }

    @Override
    public List<FodexItem> getSearchedPhotoItems(Context context, List<String> tagNames) {
        return generateFilteredFakeItems(true);
    }

    @Override
    public List<String> getTags(Context context, long imageId) {
        if (imageId % 2 == 0) {
            return getTags();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Cursor getMatchedTags(Context context, String words) {
        List<String> tags = getTags();
        String[] tagArray = new String[tags.size()];
        tagArray = tags.toArray(tagArray);
        return new MatrixCursor(tagArray);
    }

    @Override
    public void addTagsToPhotos(Context context, long[] imageIds, String[] tags) {
        // do nothing
    }

    @Override
    public void addSharePhotos(Context context, long[] imageIds) {
        // do nothing
    }

    @Override
    public void deleteTagFromPhoto(Context context, long imageId, String tag) {
        // do nothing
    }

    @Override
    public long getTagId(Context context, String tag) {
        return 0;
    }

    @Override
    public void syncAllPhotos(Context context) {
        // do nothing
    }

    private List<FodexItem> generateFilteredFakeItems(boolean isTagged) {
        List<FodexItem> items = generateFakeItems();

        List<FodexItem> filteredItems = new ArrayList<>();
        for (FodexItem item : items) {
            boolean isEven = item.id % 2 == 0;
            if (isEven && isTagged) {
                filteredItems.add(item);
            } else if (!isEven && !isTagged) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    private List<FodexItem> generateFakeItems() {
        Random random = new Random();
        int length = Math.max(random.nextInt(100), 20);
        List<FodexItem> items = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            items.add(
                    new FodexItem(
                            i,
                            i,
                            Calendar.getInstance().get(Calendar.SECOND),
                            "data",
                            Uri.parse(generatePlaceholderLink(250 + i, 250))));
        }
        return items;
    }

    private String generatePlaceholderLink(int width, int height) {
        return "http://placekitten.com/g/" + width + "/" + height;
    }

    private List<String> getTags() {
        return new ArrayList<String>() {{
            add("hello");
            add("world");
            add("SUPERSUPERSUPERSUPERSUPERSUPERSUPERLONGTEXT");
        }};
    }

}
