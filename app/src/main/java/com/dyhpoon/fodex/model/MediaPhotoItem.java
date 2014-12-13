package com.dyhpoon.fodex.model;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.provider.MediaStore;
import android.util.Log;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

/**
 * Created by darrenpoon on 9/12/14.
 */
public class MediaPhotoItem implements AsymmetricItem {

    private int mColumnSpan;
    private int mRowSpan;
    private String mId;
    private String mData;
    private String mDate;

    public MediaPhotoItem(String id, final int columnSpan, final int rowSpan, String data, String date) {
        this.mId = id;
        this.mColumnSpan = columnSpan;
        this.mRowSpan = rowSpan;
        this.mData = data;
        this.mDate = date;
    }

    public String getId() {
        return this.mId;
    }

    public String getData() {
        return this.mData;
    }

    public String getDate() {
        return this.mDate;
    }

    public void preloadImage(ImageSize imageSize) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .resetViewBeforeLoading(true)
                .build();
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.parseInt(mId));
        ImageLoader.getInstance().loadImage(uri.toString(), imageSize, options, null);
    }

    @Override
    public int getColumnSpan() {
        return this.mColumnSpan;
    }

    @Override
    public int getRowSpan() {
        return this.mRowSpan;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mColumnSpan);
        parcel.writeInt(this.mRowSpan);
    }

}
