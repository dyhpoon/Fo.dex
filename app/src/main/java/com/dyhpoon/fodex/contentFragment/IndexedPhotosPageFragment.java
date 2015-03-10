package com.dyhpoon.fodex.contentFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dyhpoon.fodex.data.FodexCore;
import com.dyhpoon.fodex.data.FodexItem;
import com.dyhpoon.fodex.fodexView.FodexBaseFragment;

import java.util.List;

/**
 * Created by darrenpoon on 26/2/15.
 */
public class IndexedPhotosPageFragment extends FodexBaseFragment<FodexItem> {

    private List<FodexItem> mItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FodexCore.syncAllPhotos(getActivity());
        mItems = FodexCore.getIndexedPhotoItems(getActivity());
        reload();
    }

    @Override
    protected List<FodexItem> itemsForAdapters() {
        return mItems;
    }

    @Override
    protected void onQueryTagsSubmitted(List<String> tags) {
        mItems = FodexCore.getSearchedPhotoItems(getActivity(), tags);
        reload();
    }

    @Override
    protected void onSearchEnd() {
        mItems = FodexCore.getIndexedPhotoItems(getActivity());
        reload();
    }
}
