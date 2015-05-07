package com.dyhpoon.fodex.contentFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dyhpoon.fodex.data.FodexItem;
import com.dyhpoon.fodex.fodexView.FodexBaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by darrenpoon on 8/12/14.
 */
public class SharedPhotosPageFragment extends FodexBaseFragment<FodexItem> {

    private List<FodexItem> mItems;
    private String mSearchedWords;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSearchedWords = "";
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                fodexCore.syncAllPhotos(mActivity);
                mItems = fodexCore.getSharedPhotoItems(mActivity);
                reload();
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    @Override
    protected List<FodexItem> itemsForAdapters() {
        return mItems;
    }

    @Override
    protected void onQueryTagsSubmitted(List<String> tags) {
        // get intersection of shared and tagged photos
        List<FodexItem> left = fodexCore.getSearchedPhotoItems(mActivity, tags);
        List<FodexItem> right = fodexCore.getSharedPhotoItems(mActivity);
        mItems = getIntersection(left, right);
        mSearchedWords = TextUtils.join(" ", tags);
        reload();
    }

    @Override
    protected void onSearchEnd() {
        if (!mSearchedWords.equals("")) {
            mItems = fodexCore.getSharedPhotoItems(mActivity);
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

        fodexCore.syncAllPhotos(mActivity);
        mItems = fodexCore.getSharedPhotoItems(mActivity);
        isTaskCompleted.set(true);
        if (isTimeIsUp.get()) {
            refreshComplete();
        }
    }

    private List<FodexItem> getIntersection(List<FodexItem> left, List<FodexItem> right) {
        List<FodexItem> intersection = new ArrayList<>();
        for (FodexItem item: left) {
            if (right.contains(item)) {
                intersection.add(item);
            }
        }
        return intersection;
    }

}
