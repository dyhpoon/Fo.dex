package com.dyhpoon.fodex.di;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.dyhpoon.fodex.FodexApplication;

/**
 * Created by darrenpoon on 28/3/15.
 */
public class BaseFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FodexApplication fodexApp = (FodexApplication) getActivity().getApplication();
        fodexApp.injectMock(this);
    }
}
