package com.dyhpoon.fodex.di;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.android.debug.hv.ViewServer;
import com.dyhpoon.fodex.BuildConfig;
import com.dyhpoon.fodex.FodexApplication;
import com.dyhpoon.fodex.data.FodexCore;

import javax.inject.Inject;

/**
 * Created by darrenpoon on 28/3/15.
 */
public class BaseFragmentActivity extends FragmentActivity {

    @Inject public FodexCore fodexCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FodexApplication fodexApp = (FodexApplication) getApplication();
        fodexApp.injectMock(this);

        if (BuildConfig.DEBUG) {
            ViewServer.get(this).addWindow(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) {
            ViewServer.get(this).setFocusedWindow(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) {
            ViewServer.get(this).removeWindow(this);
        }
    }
}
