package com.dyhpoon.fodex.contentFragment;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.dyhpoon.fodex.data.FodexCursor;
import com.dyhpoon.fodex.data.FodexItem;
import com.dyhpoon.fodex.fodexView.FodexBaseFragment;
import com.dyhpoon.fodex.util.OnCompleteListener;

import java.util.List;

/**
 * Created by darrenpoon on 8/12/14.
 */
public class AllPhotosPageFragment extends FodexBaseFragment<FodexItem> {

    private List<FodexItem> mItems;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FodexCursor.syncAllPhotos(getActivity(), new OnCompleteListener() {
            @Override
            public void didComplete() {
                mItems = FodexCursor.getAllPhotoItems(getActivity());
                reload();
            }

            @Override
            public void didFail() {
                // TODO: show error toast
            }
        });
    }

    @Override
    protected void onClickConfirmedButton(List<FodexItem> selectedItems) {
        // TODO: implement
    }

    @Override
    protected Uri imageUriForItems(FodexItem item) {
        return ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                item.imageId);
    }

    @Override
    protected List<FodexItem> itemsForAdapters() {
        return mItems;
    }

}
