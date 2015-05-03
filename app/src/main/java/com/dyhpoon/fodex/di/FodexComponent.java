package com.dyhpoon.fodex.di;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by darrenpoon on 2/5/15.
 */
@Singleton
@Component(modules = FodexCoreModule.class)
public interface FodexComponent {
    void inject(BaseFragment fragment);
    void inject(BaseFragmentActivity activity);
}
