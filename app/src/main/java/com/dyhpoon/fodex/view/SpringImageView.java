package com.dyhpoon.fodex.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

/**
 * Created by darrenpoon on 28/1/15.
 */
public class SpringImageView extends ImageView {

    private static final double TENSION = 800;
    private static final double FRICTION = 20;

    private SpringSystem mSpringSystem;
    private Spring mScaleSpring;
    private ScaleSpringListener mListener = new ScaleSpringListener();

    public SpringImageView(Context context) {
        super(context);
        init();
    }

    public SpringImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpringImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mScaleSpring.addListener(mListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mScaleSpring.removeListener(mListener);
    }

    private void init() {
        mSpringSystem = SpringSystem.create();
        mScaleSpring = mSpringSystem.createSpring();

        SpringConfig config = new SpringConfig(TENSION, FRICTION);
        mScaleSpring.setSpringConfig(config);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("HELLO", v.toString());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mScaleSpring.setEndValue(1f);
                        return true;
                    case MotionEvent.ACTION_UP:
                        mScaleSpring.setEndValue(0f);
                        return true;
                }
                return false;
            }
        });
    }

    private class ScaleSpringListener extends SimpleSpringListener {
        @Override
        public void onSpringUpdate(Spring spring) {
            float value = (float) spring.getCurrentValue();
            float scale = 1f - (value * 0.5f);
            setScaleX(scale);
            setScaleY(scale);
        }
    }

}
