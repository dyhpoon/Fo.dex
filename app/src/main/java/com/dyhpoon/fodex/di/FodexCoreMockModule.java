package com.dyhpoon.fodex.di;

import com.dyhpoon.fodex.data.FodexCore;
import com.dyhpoon.fodex.data.mock.FodexCoreMockImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by darrenpoon on 29/3/15.
 */
@Module(
        includes = FodexCoreModule.class,
        overrides = true
)
public class FodexCoreMockModule {
    @Provides
    @Singleton
    FodexCore provideFodexCore() {
        return new FodexCoreMockImpl();
    }
}
