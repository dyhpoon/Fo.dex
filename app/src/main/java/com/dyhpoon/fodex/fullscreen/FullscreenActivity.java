package com.dyhpoon.fodex.fullscreen;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.data.FodexImageContract;
import com.dyhpoon.fodex.data.FodexItem;
import com.dyhpoon.fodex.share.FacebookSharing;
import com.dyhpoon.fodex.share.GoogleSharing;
import com.dyhpoon.fodex.share.InstagramSharing;
import com.dyhpoon.fodex.share.Sharing;
import com.dyhpoon.fodex.share.WhatsappSharing;
import com.dyhpoon.fodex.util.MediaImage;
import com.dyhpoon.fodex.util.OnCompleteListener;
import com.dyhpoon.fodex.util.OnFinishListener;
import com.dyhpoon.fodex.util.SimpleAnimatorListener;
import com.dyhpoon.fodex.view.PagerContainer;
import com.dyhpoon.fodex.view.PleaseInstallToast;
import com.dyhpoon.fodex.view.ShareActionMenu;
import com.dyhpoon.fodex.view.TouchImageView;
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
    public static final String ITEMS_INFO       = PREFIX + ".ITEMS_INFO";

    private int mImageIndex;
    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;
    private boolean mIsSharing;
    private List<FodexItem> mFodexItems;

    private ViewSwitcher mSwitcher;
    private ImageView mFakeImageView;
    private PagerContainer mContainer;
    private FullscreenViewPager mPager;
    private ShareActionMenu mShareActionMenu;

    final private ColorDrawable mBackground = new ColorDrawable(Color.BLACK);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        Bundle bundle = getIntent().getExtras();
        mImageIndex =
                (savedInstanceState == null)
                        ? bundle.getInt(RESOURCE_INDEX, 0)
                        : savedInstanceState.getInt(RESOURCE_INDEX, 0);
        
        final String url    = bundle.getString(RESOURCE_URL);
        final int top       = bundle.getInt(TOP);
        final int left      = bundle.getInt(LEFT);
        final int width     = bundle.getInt(WIDTH);
        final int height    = bundle.getInt(HEIGHT);
        mFodexItems = getIntent().getParcelableArrayListExtra(ITEMS_INFO);

        setupViewSwitcher();
        setupFullscreenPager(mFodexItems, mImageIndex);
        setupFakeView(url);
        setupShareActionMenu();
        if (savedInstanceState == null) {
            mBackground.setAlpha(0);    // prevent flashing
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
                            endFakeView();
                        }
                    });

                    return true;
                }
            });
        } else {
            mSwitcher.showNext();
            endFakeView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsSharing = false;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, android.R.anim.fade_out);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(RESOURCE_INDEX, mPager.getCurrentItem());
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

    private void setupFakeView(String imageUrl) {
        mFakeImageView = (ImageView) findViewById(R.id.fake_image_view);
        mFakeImageView.setMinimumHeight(FodexImageContract.preferredMinimumHeight(FullscreenActivity.this));

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        Bitmap bm = MediaImage.getDecodedBitmap(FullscreenActivity.this, Uri.parse(imageUrl), width, height);
        mFakeImageView.setImageBitmap(bm);
    }

    private void endFakeView() {
        Bitmap bitmap = ((BitmapDrawable) mFakeImageView.getDrawable()).getBitmap();
        mFakeImageView.setImageDrawable(null);
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    private void setupShareActionMenu() {
        mShareActionMenu = new ShareActionMenu(this, FloatingActionButton.POSITION_CENTER);
        mShareActionMenu.setOnClickListener(new ShareActionMenu.OnClickListener() {
            @Override
            public void onClick(final ShareActionMenu.ShareType type) {
                if (mIsSharing) return; // ignore onClick if startActivity is already called

                // listener for sharing activity
                OnCompleteListener completeListener = new OnCompleteListener() {
                    @Override
                    public void didComplete() {
                        mIsSharing = true;
                        // TODO: update DB
                    }

                    @Override
                    public void didFail() {
                        String message;
                        switch (type) {
                            case WHATSAPP:
                                message = getString(R.string.message_please_install_whatsapp);
                                break;
                            case FACEBOOK:
                                message = getString(R.string.message_please_install_facebook);
                                break;
                            case GOOGLE:
                                message = getString(R.string.message_please_install_google);
                                break;
                            case INSTAGRAM:
                                message = getString(R.string.message_please_install_instagram);
                                break;
                            default:
                                throw new IllegalArgumentException("Unhandled type: " + type);
                        }
                        PleaseInstallToast
                                .make(FullscreenActivity.this, message)
                                .show();
                    }
                };

                // start sharing activity
                Sharing social;
                switch (type) {
                    case FACEBOOK:
                        social = new FacebookSharing();
                        break;
                    case WHATSAPP:
                        social = new WhatsappSharing();
                        break;
                    case GOOGLE:
                        social = new GoogleSharing();
                        break;
                    case INSTAGRAM:
                        social = new InstagramSharing();
                        break;
                    default:
                        throw new IllegalArgumentException("Unhandled type: " + type);
                }
                Uri uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        mFodexItems.get(mPager.getCurrentItem()).imageId);
                social.shareImage(FullscreenActivity.this, uri, completeListener);
            }
        });
    }

    private void setupFullscreenPager(final List<FodexItem> items, int position) {
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
                return items.size();
            }

            @Override
            public Uri imageUriAtPosition(int position) {
                return ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        items.get(position).imageId);
            }

            @Override
            public void onLongClick(int position) {
                mShareActionMenu.open();
            }

            @Override
            public void onClick(int position) {
                mShareActionMenu.close();
            }
        });
        mPager.setCurrentItem(position);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                mShareActionMenu.close();
            }
        });
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
            view = ((ReusableFullscreenAdapter) adapter).currentView;
        }
        return view;
    }

}
