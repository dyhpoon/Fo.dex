package com.dyhpoon.fodex.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by darrenpoon on 18/2/15.
 */
public class FodexItem implements Parcelable {

    public int imageId;
    public long date;
    public String data;

    public FodexItem(int imageId, long date, String data) {
        this.imageId = imageId;
        this.date = date;
        this.data = data;
    }

    @Override
    public String toString() {
        return "\n" + super.toString() +
                "\n imageId: " + imageId +
                "\n date: " + date +
                "\n data: " + data;
    }

    public FodexItem(Parcel in) {
        this.imageId = in.readInt();
        this.date = in.readLong();
        this.data = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.imageId);
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
