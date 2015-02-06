package com.dyhpoon.fodex.fodexView;

import com.bumptech.glide.ListPreloader;

/**
 * Created by darrenpoon on 6/2/15.
 */
public abstract class FodexPreloadSizeProvider <T> implements ListPreloader.PreloadSizeProvider {

    public abstract int[] getSize(T item);

    @Override
    public int[] getPreloadSize(Object item, int adapterPosition, int perItemPosition) {
        return getSize((T)item);
    }
}
