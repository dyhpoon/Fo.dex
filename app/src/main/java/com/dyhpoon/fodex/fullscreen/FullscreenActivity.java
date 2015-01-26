package com.dyhpoon.fodex.fullscreen;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.dyhpoon.fodex.R;

import java.io.IOException;

public class FullscreenActivity extends Activity {

    private ViewPager mPager;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mPager = (ViewPager) findViewById(R.id.pager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCursor = getAdapter();
        mPager.setAdapter(new ImageAdapter(this) {

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

    private Cursor getAdapter() {
        ContentResolver resolver = getContentResolver();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
        };
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
        );
        return cursor;
    }

}
