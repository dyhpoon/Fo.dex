package com.dyhpoon.fodex.fodexView;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.dyhpoon.fodex.R;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;

/**
 * Created by darrenpoon on 11/3/15.
 */
public class AsymmetricSizeProvider extends FodexPreloadSizeProvider <FodexLayoutSpecItem> {

    private AsymmetricGridViewAdapter mAdapter;
    final int leftMargin, topMargin, rightMargin, bottomMargin;

    public AsymmetricSizeProvider(@NonNull Context context, @NonNull AsymmetricGridViewAdapter adapter) {
        this.mAdapter = adapter;

        Resources res = context.getResources();
        leftMargin      = res.getDimensionPixelSize(R.dimen.grid_item_left_margin);
        topMargin       = res.getDimensionPixelSize(R.dimen.grid_item_top_margin);
        rightMargin     = res.getDimensionPixelSize(R.dimen.grid_item_right_margin);
        bottomMargin    = res.getDimensionPixelSize(R.dimen.grid_item_bottom_margin);
    }

    @Override
    public int[] getSize(FodexLayoutSpecItem item) {
        return new int[]{
                mAdapter.getRowWidth(item) - leftMargin - rightMargin,
                mAdapter.getRowHeight(item) - topMargin - bottomMargin};
    }
}
