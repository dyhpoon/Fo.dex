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
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.data.FodexCursor;
import com.dyhpoon.fodex.data.FodexImageContract;
import com.dyhpoon.fodex.view.PagerContainer;
import com.dyhpoon.fodex.view.TouchImageView;

import java.io.IOException;

public class FullscreenActivity extends Activity {

    private static final int ANIM_DURATION = 300;
    private static final String PREFIX = FullscreenActivity.class.getName();
    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();

    public static final String RESOURCE_INDEX   = PREFIX + ".RESOURCE_INDEX";
    public static final String TOP              = PREFIX + ".TOP";
    public static final String LEFT             = PREFIX + ".LEFT";
    public static final String WIDTH            = PREFIX + ".WIDTH";
    public static final String HEIGHT           = PREFIX + ".HEIGHT";

    private int mImageIndex;
    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;

    private ViewSwitcher mSwitcher;
    private ImageView mFakeImageView;
    private PagerContainer mContainer;
    private FullscreenViewPager mPager;

    private Cursor mCursor;
    private ColorDrawable mBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mCursor = FodexCursor.allPhotosCursor(FullscreenActivity.this);

        Bundle bundle = getIntent().getExtras();
        mImageIndex         = bundle.getInt(RESOURCE_INDEX, 0);
        final int top       = bundle.getInt(TOP);
        final int left      = bundle.getInt(LEFT);
        final int width     = bundle.getInt(WIDTH);
        final int height    = bundle.getInt(HEIGHT);

        setupViewSwitcher();
        setupFakeView(mCursor, mImageIndex);
        setupFullscreenPager(mCursor, mImageIndex);

        if (savedInstanceState == null) {
            ViewTreeObserver observer = mPager.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mFakeImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    int screenLocation[] = new int[2];
                    mFakeImageView.getLocationOnScreen(screenLocation);
                    mLeftDelta = left - screenLocation[0];
                    mTopDelta = top - screenLocation[1];

                    mWidthScale = (float) width / mFakeImageView.getWidth();
                    mHeightScale = (float) height / mFakeImageView.getHeight();

                    runEnterAnimation();

                    return true;
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCursor.close();
    }

    @TargetApi(16)
    private void setupViewSwitcher() {
        mSwitcher = (ViewSwitcher) findViewById(R.id.switcher);
        mBackground = new ColorDrawable(Color.BLACK);
        int currentVersion = Build.VERSION.SDK_INT;
        if (currentVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            mSwitcher.setBackground(mBackground);
        } else {
            mSwitcher.setBackgroundDrawable(mBackground);
        }
    }

    private void setupFakeView(Cursor cursor, int position) {
        Bitmap bm = getImageFromCursor(cursor, position);
        mFakeImageView = (ImageView) findViewById(R.id.fake_image_view);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mFakeImageView.getLayoutParams();
        lp.setMargins(
                FodexImageContract.LEFT_MARGIN,
                FodexImageContract.TOP_MARGIN,
                FodexImageContract.RIGHT_MARGIN,
                FodexImageContract.BOTTOM_MARGIN);
        mFakeImageView.setLayoutParams(lp);
        mFakeImageView.setMinimumHeight(FodexImageContract.preferredMinimumHeight(FullscreenActivity.this));
        mFakeImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mFakeImageView.setImageBitmap(bm);
    }

    private void setupFullscreenPager(final Cursor cursor, int position) {
        mContainer = (PagerContainer) findViewById(R.id.pager_container);
        mPager = (FullscreenViewPager) mContainer.getViewPager();
        mPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.fullscreen_pager_padding));
        mPager.setClipChildren(false);
        mPager.setVerticalScrollBarEnabled(false);
        mPager.setCanScrollAdapter(new FullscreenViewPager.CanScrollAdapter() {
            @Override
            public boolean canScroll() {
                return !((TouchImageView)getCurrentPagerView()).isZoomed();
            }
        });
        mPager.setAdapter(new ReusableFullscreenAdapter(this) {
            @Override
            public Bitmap imageBitmapAtPosition(int position) {
                return getImageFromCursor(cursor, position);
            }

            @Override
            public int getCount() {
                return cursor.getCount();
            }
        });
        mPager.setCurrentItem(position);
    }

    private void runEnterAnimation() {
        final long duration = (long) (ANIM_DURATION * 1);

        mFakeImageView.setPivotX(0);
        mFakeImageView.setPivotY(0);
        mFakeImageView.setScaleX(mWidthScale);
        mFakeImageView.setScaleY(mHeightScale);
        mFakeImageView.setTranslationX(mLeftDelta);
        mFakeImageView.setTranslationY(mTopDelta);

        mFakeImageView.animate()
                .setDuration(duration)
                .scaleX(1)
                .scaleY(1)
                .translationX(0)
                .translationY(0)
                .setInterpolator(sDecelerator);

        // Fade in the black background
        ObjectAnimator bgAnimation = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        bgAnimation.setDuration((long) (duration * 0.75));
        bgAnimation.start();
    }

    private TouchImageView getCurrentPagerView() {
        TouchImageView view = null;
        PagerAdapter adapter = mPager.getAdapter();
        if (adapter != null) {
            view = ((ReusableFullscreenAdapter)adapter).currentView;
        }
        return view;
    }

    private Bitmap getImageFromCursor(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        final int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        String id = cursor.getString(idColumn);
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
}
