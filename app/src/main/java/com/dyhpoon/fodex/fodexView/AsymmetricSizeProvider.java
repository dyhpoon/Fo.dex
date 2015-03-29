package com.dyhpoon.fodex.fodexView;

import android.support.annotation.NonNull;

import com.dyhpoon.fodex.data.actual.FodexImageContract;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;

/**
 * Created by darrenpoon on 11/3/15.
 */
public class AsymmetricSizeProvider extends FodexPreloadSizeProvider <FodexLayoutSpecItem> {

    private AsymmetricGridViewAdapter mAdapter;

    public AsymmetricSizeProvider(@NonNull AsymmetricGridViewAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public int[] getSize(FodexLayoutSpecItem item) {
        return new int[]{
                mAdapter.getRowWidth(item) - FodexImageContract.LEFT_MARGIN - FodexImageContract.RIGHT_MARGIN,
                mAdapter.getRowHeight(item) - FodexImageContract.TOP_MARGIN - FodexImageContract.BOTTOM_MARGIN};
    }
}
