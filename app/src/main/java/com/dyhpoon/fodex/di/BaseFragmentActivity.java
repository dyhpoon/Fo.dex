package com.dyhpoon.fodex.di;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.dyhpoon.fodex.FodexApplication;

/**
 * Created by darrenpoon on 28/3/15.
 */
public class BaseFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FodexApplication fodexApp = (FodexApplication) getApplication();
        fodexApp.injectMock(this);
    }
}
