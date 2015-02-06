package com.dyhpoon.fodex.fodexView;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

/**
 * Created by darrenpoon on 6/2/15.
 */
public class FodexGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Do nothing.
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(FodexLayoutSpecItem.class, InputStream.class, new FodexModelLoader.Factory());
    }
}
