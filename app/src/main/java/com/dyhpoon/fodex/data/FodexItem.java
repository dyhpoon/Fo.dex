package com.dyhpoon.fodex.data;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

/**
 * Created by darrenpoon on 18/2/15.
 */
public class FodexItem implements Parcelable {

    public long id;
    public long imageId;
    public long date;
    public String data;
    public Uri uri;

    public FodexItem(long id, long imageId, long date, String data) {
        this.id = id;
        this.imageId = imageId;
        this.date = date;
        this.data = data;
        uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);
    }

    @Override
    public String toString() {
        return "\n" + super.toString() +
                "\n id: " + id +
                "\n imageId: " + imageId +
                "\n date: " + date +
                "\n data: " + data;
    }

    public FodexItem(Parcel in) {
        this.id = in.readLong();
        this.imageId = in.readLong();
        this.date = in.readLong();
        this.data = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.imageId);
        dest.writeLong(this.date);
        dest.writeString(this.data);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Object createFromParcel(Parcel source) {
            return new FodexItem(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new FodexItem[size];
        }
    };
}
