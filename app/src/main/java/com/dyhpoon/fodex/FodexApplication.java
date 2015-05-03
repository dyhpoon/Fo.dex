package com.dyhpoon.fodex;

import android.app.Application;

import com.dyhpoon.fodex.data.actual.FodexCoreImpl;
import com.dyhpoon.fodex.data.mock.FodexCoreMockImpl;
import com.dyhpoon.fodex.di.BaseFragment;
import com.dyhpoon.fodex.di.BaseFragmentActivity;
import com.dyhpoon.fodex.di.DaggerFodexComponent;
import com.dyhpoon.fodex.di.FodexComponent;
import com.dyhpoon.fodex.di.FodexCoreModule;
import com.facebook.stetho.Stetho;

/**
 * Created by darrenpoon on 19/2/15.
 */
public class FodexApplication extends Application {

    private boolean isMockMode = false;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build());
        }
    }

    public void injectMock(BaseFragment fragment) {
        injectMock(isMockMode, fragment);
    }

    public void injectMock(BaseFragmentActivity activity) {
        injectMock(isMockMode, activity);
    }

    public void injectMock(boolean isMock, BaseFragment fragment) {
        FodexComponent component = getComponent(isMock);
        component.inject(fragment);
    }

    public void injectMock(boolean isMock, BaseFragmentActivity activity) {
        FodexComponent component = getComponent(isMock);
        component.inject(activity);
    }

    public FodexComponent getComponent(boolean isMock) {
        isMockMode = isMock;

        FodexCoreModule module;
        if (BuildConfig.DEBUG && isMockMode) {
            module = new FodexCoreModule(new FodexCoreMockImpl());
        } else {
            module = new FodexCoreModule(new FodexCoreImpl());
        }
        return DaggerFodexComponent.builder()
                .fodexCoreModule(module)
                .build();
    }

}
