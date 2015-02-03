package com.dyhpoon.fodex.fullscreen;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.data.FodexCursor;
import com.dyhpoon.fodex.view.PagerContainer;
import com.dyhpoon.fodex.view.TouchImageView;

import java.io.IOException;

public class FullscreenActivity extends Activity {

    private static final String PREFIX = FullscreenActivity.class.getName();
    public static final String RESOURCE_INDEX =  PREFIX + ".RESOURCE_INDEX";

    private PagerContainer mContainer;
    private FullscreenViewPager mPager;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mContainer = (PagerContainer) findViewById(R.id.pager_container);

        mCursor = FodexCursor.allPhotosCursor(FullscreenActivity.this);

        mPager = (FullscreenViewPager) mContainer.getViewPager();
        mPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.fullscreen_pager_padding));
        mPager.setClipChildren(false);
        mPager.setVerticalScrollBarEnabled(false);
        mPager.setCanScrollAdapter(new FullscreenViewPager.CanScrollAdapter() {
            @Override
            public boolean canScroll() {
                View currentView = ((ReusableFullscreenAdapter)mPager.getAdapter()).currentView;
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
        int imageIndex = bundle.getInt(RESOURCE_INDEX, 0);
        mPager.setCurrentItem(imageIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCursor.close();
    }
}
