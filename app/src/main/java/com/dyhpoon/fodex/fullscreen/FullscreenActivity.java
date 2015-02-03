package com.dyhpoon.fodex.fullscreen;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.data.FodexCursor;
import com.dyhpoon.fodex.view.PagerContainer;
import com.dyhpoon.fodex.view.TouchImageView;

import java.io.IOException;

public class FullscreenActivity extends Activity {

    private static final String PREFIX = FullscreenActivity.class.getName();
    private static final int ANIM_DURATION = 500;
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();

    public static final String RESOURCE_INDEX   = PREFIX + ".RESOURCE_INDEX";
    public static final String TOP              = PREFIX + ".TOP";
    public static final String LEFT             = PREFIX + ".LEFT";
    public static final String WIDTH            = PREFIX + ".WIDTH";
    public static final String HEIGHT           = PREFIX + ".HEIGHT";

    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;
    private ColorDrawable mBackground;
    private RelativeLayout mTopLayout;
    private ViewSwitcher mSwitcher;

    private PagerContainer mContainer;
    private FullscreenViewPager mPager;
    private Cursor mCursor;

    @TargetApi(16)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mSwitcher = (ViewSwitcher) findViewById(R.id.switcher);
        mTopLayout = (RelativeLayout) findViewById(R.id.top_layout);
        mContainer = (PagerContainer) findViewById(R.id.pager_container);
        mSwitcher.showNext();

        mBackground = new ColorDrawable(Color.BLACK);
        int currentVersion = Build.VERSION.SDK_INT;
        if (currentVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            mTopLayout.setBackground(mBackground);
        } else {
            mTopLayout.setBackgroundDrawable(mBackground);
        }

        mCursor = FodexCursor.allPhotosCursor(FullscreenActivity.this);

        mPager = (FullscreenViewPager) mContainer.getViewPager();
        mPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.fullscreen_pager_padding));
        mPager.setClipChildren(false);
        mPager.setVerticalScrollBarEnabled(false);
        mPager.setCanScrollAdapter(new FullscreenViewPager.CanScrollAdapter() {
            @Override
            public boolean canScroll() {
                View currentView = getCurrentView();
                return !((TouchImageView)currentView).isZoomed();
            }
        });
        mPager.setAdapter(new ReusableFullscreenAdapter(this) {
            @Override
            public Bitmap imageBitmapAtPosition(int position) {
                mCursor.moveToPosition(position);
                final int idColumn = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                String id = mCursor.getString(idColumn);
                Uri uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        Integer.parseInt(id));

                Bitmap bm = null;
                try {
                    bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bm;
            }

            @Override
            public int getCount() {
                return mCursor.getCount();
            }
        });

        Bundle bundle = getIntent().getExtras();
//        final int top           = bundle.getInt(TOP);
//        final int left          = bundle.getInt(LEFT);
//        final int width         = bundle.getInt(WIDTH);
//        final int height        = bundle.getInt(HEIGHT);
//        final int imageIndex    = bundle.getInt(RESOURCE_INDEX, 0);
//
//        mPager.setCurrentItem(imageIndex);
//
//
//        if (savedInstanceState == null) {
//            ViewTreeObserver observer = mPager.getViewTreeObserver();
//            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    mPager.getViewTreeObserver().removeOnPreDrawListener(this);
//
//                    int screenLocation[] = new int[2];
//                    mPager.getLocationOnScreen(screenLocation);
//                    mLeftDelta = left - screenLocation[0];
//                    mTopDelta = top - screenLocation[1];
//
//                    mWidthScale = width / mPager.getWidth();
//                    mHeightScale = height / mPager.getHeight();
//
//                    runEnterAnimation();
//
//                    return true;
//                }
//            });
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCursor.close();
    }

    private void runEnterAnimation() {
        final long duration = (long) (ANIM_DURATION * 3);

        mPager.setPivotX(0);
        mPager.setPivotY(0);
        mPager.setScaleX(mWidthScale);
        mPager.setScaleY(mHeightScale);
        mPager.setTranslationX(mLeftDelta);
        mPager.setTranslationY(mTopDelta);

        mPager.animate()
                .setDuration(duration)
                .scaleX(1)
                .scaleY(1)
                .translationX(0)
                .translationY(0)
                .setInterpolator(sDecelerator);

        // Fade in the black background
        ObjectAnimator bgAnimation = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        bgAnimation.setDuration(duration);
        bgAnimation.start();
    }

    private View getCurrentView() {
        View view = null;
        PagerAdapter adapter = mPager.getAdapter();
        if (adapter != null) {
            view = ((ReusableFullscreenAdapter)adapter).currentView;
        }
        return view;
    }
}
