package com.dyhpoon.fodex.contentFragment;

import android.os.Bundle;

import com.dyhpoon.fodex.data.FodexCursor;
import com.dyhpoon.fodex.data.FodexItem;
import com.dyhpoon.fodex.fodexView.FodexBaseFragment;
import com.dyhpoon.fodex.util.OnCompleteListener;

import java.util.List;

/**
 * Created by darrenpoon on 26/2/15.
 */
public class UnindexedPhotoPageFragment extends FodexBaseFragment<FodexItem> {

    private List<FodexItem> mItems;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FodexCursor.syncAllPhotos(getActivity(), new OnCompleteListener() {
            @Override
            public void didComplete() {
                mItems = FodexCursor.getUnindexPhotoItems(getActivity());
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
