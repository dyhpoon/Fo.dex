package com.dyhpoon.fodex;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by darrenpoon on 19/2/15.
 */
public class FodexApplication extends Application {
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
}
