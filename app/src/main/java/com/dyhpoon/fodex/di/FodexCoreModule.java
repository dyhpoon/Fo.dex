package com.dyhpoon.fodex.di;

import com.dyhpoon.fodex.contentFragment.AllPhotosPageFragment;
import com.dyhpoon.fodex.contentFragment.IndexedPhotosPageFragment;
import com.dyhpoon.fodex.contentFragment.RecentPhotosPageFragment;
import com.dyhpoon.fodex.contentFragment.UnindexedPhotoPageFragment;
import com.dyhpoon.fodex.data.FodexCore;
import com.dyhpoon.fodex.data.actual.FodexCoreImpl;
import com.dyhpoon.fodex.fullscreen.FullscreenActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by darrenpoon on 28/3/15.
 */
@Module(
        injects = {
                AllPhotosPageFragment.class,
                IndexedPhotosPageFragment.class,
                RecentPhotosPageFragment.class,
                UnindexedPhotoPageFragment.class,
                FullscreenActivity.class,
        },
        library = true)
public class FodexCoreModule {
    @Provides
    @Singleton
    FodexCore provideFodexCore() {
        return new FodexCoreImpl();
    }
}
