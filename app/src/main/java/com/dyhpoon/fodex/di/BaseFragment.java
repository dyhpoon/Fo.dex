package com.dyhpoon.fodex.di;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import dagger.ObjectGraph;

/**
 * Created by darrenpoon on 28/3/15.
 */
public class BaseFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObjectGraph.create(new FodexCoreModule()).inject(this);
    }
}
