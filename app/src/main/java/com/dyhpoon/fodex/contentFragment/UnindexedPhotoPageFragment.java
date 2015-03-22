package com.dyhpoon.fodex.contentFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
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
 * Created by darrenpoon on 26/2/15.
 */
public class UnindexedPhotoPageFragment extends FodexBaseFragment<FodexItem> {

    private List<FodexItem> mItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FodexCore.syncAllPhotos(getActivity());
        mItems = FodexCore.getUnindexPhotoItems(getActivity());
        reload();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // not to show searchView in this page.
        menu.clear();
    }

    @Override
    protected List<FodexItem> itemsForAdapters() {
        return mItems;
    }

    @Override
    protected void onQueryTagsSubmitted(List<String> tags) {
        // do nothing
    }

    @Override
    protected void onSearchEnd() {
        // do nothing
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
        mItems = FodexCore.getUnindexPhotoItems(getActivity());
        isTaskCompleted.set(true);
        if (isTimeIsUp.get()) {
            refreshComplete();
        }
    }

}
