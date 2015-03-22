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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by darrenpoon on 8/12/14.
 */
public class AllPhotosPageFragment extends FodexBaseFragment<FodexItem> {

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
        mItems = FodexCore.getAllPhotoItems(getActivity());
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
            mItems = FodexCore.getAllPhotoItems(getActivity());
            reload();
            mSearchedWords = "";
        }
    }

    @Override
    protected void onRefreshBegin() {
        final AtomicReference<Boolean> isTaskCompleted = new AtomicReference<>(false);
        final AtomicReference<Boolean> isTimeIsUp = new AtomicReference<>(false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isTimeIsUp.set(true);
                if (isTaskCompleted.get()) {
                    refreshComplete();
                }
            }
        }, 1300);   // set minimum loading time

        FodexCore.syncAllPhotos(getActivity());
        mItems = FodexCore.getAllPhotoItems(getActivity());
        isTaskCompleted.set(true);
        if (isTimeIsUp.get()) {
            refreshComplete();
        }
    }

}
