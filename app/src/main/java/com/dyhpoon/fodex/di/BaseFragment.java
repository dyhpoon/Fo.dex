package com.dyhpoon.fodex.di;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.android.debug.hv.ViewServer;
import com.dyhpoon.fodex.BuildConfig;
import com.dyhpoon.fodex.FodexApplication;

import dagger.ObjectGraph;

/**
 * Created by darrenpoon on 28/3/15.
 */
public class BaseFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FodexApplication fodexApp = (FodexApplication) getActivity().getApplication();
        fodexApp.injectMock(this);

        ObjectGraph.create(new FodexCoreModule()).inject(this);
        if (BuildConfig.DEBUG) {
            ViewServer.get(getActivity()).addWindow(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) {
            ViewServer.get(getActivity()).setFocusedWindow(getActivity());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) {
            ViewServer.get(getActivity()).removeWindow(getActivity());
        }
    }
}
