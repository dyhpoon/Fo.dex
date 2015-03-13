package com.dyhpoon.fodex.contentFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
    private String mSearchedWords;

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
        mSearchedWords = "";
    }

    @Override
    protected List<FodexItem> itemsForAdapters() {
        return mItems;
    }

    @Override
    protected void onQueryTagsSubmitted(List<String> tags) {
        mItems = FodexCore.getSearchedPhotoItems(getActivity(), tags);
        mSearchedWords = TextUtils.join(" ", tags);
        reload();
    }

    @Override
    protected void onSearchEnd() {
        if (!mSearchedWords.equals("")) {
            mItems = FodexCore.getIndexedPhotoItems(getActivity());
            reload();
            mSearchedWords = "";
        }
    }
}
