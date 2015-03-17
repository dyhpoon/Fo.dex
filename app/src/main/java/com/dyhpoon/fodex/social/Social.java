package com.dyhpoon.fodex.social;

import android.app.Activity;

import com.dyhpoon.fodex.util.OnCompleteListener;

/**
 * Created by darrenpoon on 17/3/15.
 */
public interface Social {
    public boolean isActive();
    public void login(Activity activity, OnCompleteListener listener);
}
