package com.dyhpoon.fodex.di;

import com.dyhpoon.fodex.data.FodexCore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by darrenpoon on 28/3/15.
 */
@Module
public class FodexCoreModule {

    private FodexCore mFodexCore;

    public FodexCoreModule(FodexCore core) {
        if (core == null) {
            throw new RuntimeException("Expect FodexCore to be not null");
        }
        mFodexCore = core;
    }

    @Singleton
    @Provides
    FodexCore provideFodexCore() {
        return mFodexCore;
    }
}
