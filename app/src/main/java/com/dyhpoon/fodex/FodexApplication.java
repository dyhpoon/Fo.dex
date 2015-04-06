package com.dyhpoon.fodex;

import android.app.Application;

import com.dyhpoon.fodex.di.FodexCoreMockModule;
import com.dyhpoon.fodex.di.FodexCoreModule;
import com.facebook.stetho.Stetho;

import dagger.ObjectGraph;

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

    public <T> void injectMock(T instance) {
        injectMock(isMockMode, instance);
    }

    public <T> void injectMock(boolean isMock, T instance) {
        isMockMode = isMock;

        ObjectGraph objectGraph;
        if (BuildConfig.DEBUG && isMockMode) {
            objectGraph = ObjectGraph.create(new FodexCoreMockModule());
        } else {
            objectGraph = ObjectGraph.create(new FodexCoreModule());
        }
        objectGraph.inject(instance);
    }
}
