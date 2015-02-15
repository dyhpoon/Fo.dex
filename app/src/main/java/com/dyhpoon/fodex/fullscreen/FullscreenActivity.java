package com.dyhpoon.fodex.fullscreen;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.data.MediaCursor;
import com.dyhpoon.fodex.data.FodexImageContract;
import com.dyhpoon.fodex.util.OnFinishListener;
import com.dyhpoon.fodex.util.SimpleAnimatorListener;
import com.dyhpoon.fodex.view.PagerContainer;
import com.dyhpoon.fodex.view.ShareActionMenu;
import com.dyhpoon.fodex.view.TouchImageView;
import com.felipecsl.asymmetricgridview.library.widget.GridItemViewInfo;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;

import java.util.List;

public class FullscreenActivity extends Activity {

    private static final int ANIM_DURATION = 250;
    private static final String PREFIX = FullscreenActivity.class.getName();

    public static final String RESOURCE_INDEX   = PREFIX + ".RESOURCE_INDEX";
    public static final String RESOURCE_URL     = PREFIX + ".RESOURCES_URL";
    public static final String TOP              = PREFIX + ".TOP";
    public static final String LEFT             = PREFIX + ".LEFT";
    public static final String WIDTH            = PREFIX + ".WIDTH";
    public static final String HEIGHT           = PREFIX + ".HEIGHT";
    public static final String VIEWS_INFO       = PREFIX + ".VIEWS_INFO";

    private int mImageIndex;
    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;
    private List<GridItemViewInfo> viewInfos;

    private ViewSwitcher mSwitcher;
    private ImageView mFakeImageView;
    private PagerContainer mContainer;
    private FullscreenViewPager mPager;

    private Cursor mCursor;
    final private ColorDrawable mBackground = new ColorDrawable(Color.BLACK);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mCursor = MediaCursor.allPhotosCursor(FullscreenActivity.this);

        Bundle bundle = getIntent().getExtras();
        mImageIndex         = bundle.getInt(RESOURCE_INDEX, 0);
        final String url    = bundle.getString(RESOURCE_URL);
        final int top       = bundle.getInt(TOP);
        final int left      = bundle.getInt(LEFT);
        final int width     = bundle.getInt(WIDTH);
        final int height    = bundle.getInt(HEIGHT);
        viewInfos           = getIntent().getParcelableArrayListExtra(VIEWS_INFO);

        mBackground.setAlpha(0);    // prevent flashing

        setupViewSwitcher();
        setupFullscreenPager(mCursor, mImageIndex);
        setupFakeView(url, new OnFinishListener() {
            @Override
            public void didFinish() {
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

                            runEnterAnimation(new OnFinishListener() {
                                @Override
                                public void didFinish() {
                                    mSwitcher.showNext();
                                }
                            });

                            return true;
                        }
                    });
                }
            }
        });
        new ShareActionMenu(this, FloatingActionButton.POSITION_CENTER);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCursor.close();
    }

    @TargetApi(16)
    private void setupViewSwitcher() {
        mSwitcher = (ViewSwitcher) findViewById(R.id.switcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSwitcher.setBackground(mBackground);
        } else {
            mSwitcher.setBackgroundDrawable(mBackground);
        }
    }

    private void setupFakeView(String imageUrl, final OnFinishListener listener) {
        mFakeImageView = (ImageView) findViewById(R.id.fake_image_view);
        mFakeImageView.setMinimumHeight(FodexImageContract.preferredMinimumHeight(FullscreenActivity.this));

        Glide.with(FullscreenActivity.this)
                .loadFromMediaStore(Uri.parse(imageUrl))
                .priority(Priority.IMMEDIATE)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mFakeImageView.setImageDrawable(resource);
                        if (listener != null) listener.didFinish();
                    }
                });
    }

    private void setupFullscreenPager(@NonNull final Cursor cursor, int position) {
        mContainer = (PagerContainer) findViewById(R.id.pager_container);
        mPager = (FullscreenViewPager) mContainer.getViewPager();
        mPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.fullscreen_pager_padding));
        mPager.setClipChildren(false);
        mPager.setVerticalScrollBarEnabled(false);
        mPager.setCanScrollAdapter(new FullscreenViewPager.CanScrollAdapter() {
            @Override
            public boolean canScroll() {
                return !getCurrentPagerView().isZoomed();
            }
        });
        mPager.setAdapter(new ReusableFullscreenAdapter(this) {
            @Override
            public int getCount() {
                return cursor.getCount();
            }

            @Override
            public Uri imageUriAtPosition(int position) {
                cursor.moveToPosition(position);
                final int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                String id = cursor.getString(idColumn);
                return ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        Integer.parseInt(id));
            }
        });
        mPager.setCurrentItem(position);
    }

    @TargetApi(16)
    private void runEnterAnimation(final OnFinishListener listener) {
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
                .setInterpolator(new OvershootInterpolator(1));

        // Fade in the black background
        ObjectAnimator bgAnimation = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        bgAnimation.setDuration((long) (duration * 0.5));
        bgAnimation.start();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            mFakeImageView.animate().setListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (listener != null) listener.didFinish();
                }
            });
        } else {
            mFakeImageView.animate().withEndAction(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) listener.didFinish();
                }
            });
        }
    }

    private TouchImageView getCurrentPagerView() {
        TouchImageView view = null;
        PagerAdapter adapter = mPager.getAdapter();
        if (adapter != null) {
            view = ((ReusableFullscreenAdapter)adapter).currentView;
        }
        return view;
    }

}
