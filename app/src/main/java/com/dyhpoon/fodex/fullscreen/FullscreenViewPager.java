package com.dyhpoon.fodex.fullscreen;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by darrenpoon on 30/1/15.
 */
public class FullscreenViewPager extends ViewPager {

    private CanScrollAdapter canScrollAdapter;

    public interface CanScrollAdapter {
        public boolean canScroll();
    }

    public FullscreenViewPager(Context context) {
        super(context);
    }

    public FullscreenViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (canScrollAdapter != null) {
            return canScrollAdapter.canScroll() && super.onTouchEvent(event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (canScrollAdapter != null) {
            return canScrollAdapter.canScroll() && super.onInterceptTouchEvent(event);
        } else {
            return super.onInterceptTouchEvent(event);
        }
    }

    public void setCanScrollAdapter(CanScrollAdapter adapter) {
        this.canScrollAdapter = adapter;
    }

}
