package com.dyhpoon.fodex.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

/**
 * Created by darrenpoon on 24/1/15.
 */
public class FodexLayoutSpecItem implements AsymmetricItem {
    public int columnSpan;
    public int rowSpan;
    public Object object;

    public FodexLayoutSpecItem(final int columnSpan, final int rowSpan, final Object object) {
        this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
        this.object = object;
    }

    public FodexLayoutSpecItem(final Parcel in) {
        this.columnSpan = in.readInt();
        this.rowSpan = in.readInt();
    }

    @Override
    public int getColumnSpan() {
        return this.columnSpan;
    }

    @Override
    public int getRowSpan() {
        return this.rowSpan;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.columnSpan);
        dest.writeInt(this.rowSpan);
    }

    public static final Parcelable.Creator<FodexLayoutSpecItem> CREATOR = new Parcelable.Creator<FodexLayoutSpecItem>() {
        @Override
        public FodexLayoutSpecItem createFromParcel(Parcel source) {
            return new FodexLayoutSpecItem(source);
        }

        @Override
        public FodexLayoutSpecItem[] newArray(int size) {
            return new FodexLayoutSpecItem[size];
        }
    };
}
