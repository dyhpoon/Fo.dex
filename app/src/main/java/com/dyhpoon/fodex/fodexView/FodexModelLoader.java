package com.dyhpoon.fodex.fodexView;

import android.content.Context;

import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

import java.io.InputStream;

/**
 * Created by darrenpoon on 6/2/15.
 */
public class FodexModelLoader extends BaseGlideUrlLoader<FodexLayoutSpecItem> {

    public static class Factory implements ModelLoaderFactory<FodexLayoutSpecItem, InputStream> {
        private final ModelCache<FodexLayoutSpecItem, GlideUrl> modelCache =
                new ModelCache<FodexLayoutSpecItem, GlideUrl>(500);

        @Override
        public ModelLoader<FodexLayoutSpecItem, InputStream> build(Context context, GenericLoaderFactory factories) {
            return new FodexModelLoader(context, modelCache);
        }

        @Override
        public void teardown() {
        }
    }

    public FodexModelLoader(Context context, ModelCache<FodexLayoutSpecItem, GlideUrl> modelCache) {
        super(context, modelCache);
    }

    @Override
    protected String getUrl(FodexLayoutSpecItem model, int width, int height) {
        return model.uri.toString();
    }
}
