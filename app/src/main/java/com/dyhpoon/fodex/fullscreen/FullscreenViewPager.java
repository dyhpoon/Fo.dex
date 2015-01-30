package com.dyhpoon.fodex.fullscreen;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by darrenpoon on 30/1/15.
 */
public class FullscreenViewPager extends ViewPager {

    private canScrollAdapter mAdapter;

    public interface canScrollAdapter {
        public boolean canScroll();
    }

    public FullscreenViewPager(Context context) {
        super(context);
    }

    public FullscreenViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanScrollAdapter(canScrollAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if (mAdapter != null)
            return mAdapter.canScroll();
        else
            return super.canScrollHorizontally(direction);
    }
}
