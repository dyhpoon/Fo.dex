package com.dyhpoon.fodex.fullscreen;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;

import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.data.FodexCursor;
import com.dyhpoon.fodex.view.PagerContainer;

import java.io.IOException;

public class FullscreenActivity extends Activity {

    private PagerContainer mContainer;
    private FullscreenViewPager mPager;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mContainer = (PagerContainer) findViewById(R.id.pager_container);
        mPager = (FullscreenViewPager) mContainer.getViewPager();
        mPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.fullscreen_pager_padding));
        mPager.setClipChildren(false);
        mPager.setVerticalScrollBarEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCursor = FodexCursor.allPhotosCursor(FullscreenActivity.this);
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
    }

}
