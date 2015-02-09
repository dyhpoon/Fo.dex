package com.felipecsl.asymmetricgridview.library.widget;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by darrenpoon on 9/2/15.
 */
public class GridItemViewInfo implements Parcelable {
    int index, top, left, width, height;

    public GridItemViewInfo(int index, int top, int left, int width, int height) {
        this.index = index;
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
    }

    public GridItemViewInfo(Parcel in) {
        int[] data = new int[5];
        in.readIntArray(data);
        this.index = data[0];
        this.top = data[1];
        this.left = data[2];
        this.width = data[3];
        this.height = data[4];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(new int[] {
                this.index,
                this.top,
                this.left,
                this.width,
                this.height
        });
    }

    public static final Parcelable.Creator<GridItemViewInfo> CREATOR = new Creator<GridItemViewInfo>() {
        @Override
        public GridItemViewInfo createFromParcel(Parcel source) {
            return new GridItemViewInfo(source);
        }

        @Override
        public GridItemViewInfo[] newArray(int size) {
            return new GridItemViewInfo[size];
        }
    };
}
