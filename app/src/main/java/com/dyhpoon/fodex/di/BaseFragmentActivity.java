package com.dyhpoon.fodex.di;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;

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
}
