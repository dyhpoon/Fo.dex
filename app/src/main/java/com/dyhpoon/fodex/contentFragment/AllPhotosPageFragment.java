package com.dyhpoon.fodex.contentFragment;

import android.os.Bundle;

import com.dyhpoon.fodex.data.FodexCore;
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
        FodexCore.syncAllPhotos(getActivity(), new OnCompleteListener() {
            @Override
            public void didComplete() {
                mItems = FodexCore.getAllPhotoItems(getActivity());
                reload();
            }

            @Override
            public void didFail() {
                // TODO: show error toast
            }
        });
    }

    @Override
    protected List<FodexItem> itemsForAdapters() {
        return mItems;
    }

}
