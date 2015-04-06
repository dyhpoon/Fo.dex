package com.dyhpoon.fodex.di;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;

import com.android.debug.hv.ViewServer;
import com.dyhpoon.fodex.BuildConfig;

import dagger.ObjectGraph;

/**
 * Created by darrenpoon on 28/3/15.
 */
public class BaseFragmentActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        ObjectGraph.create(new FodexCoreModule()).inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
