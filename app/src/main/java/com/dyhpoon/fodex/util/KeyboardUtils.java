package com.dyhpoon.fodex.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by darrenpoon on 12/3/15.
 */
public class KeyboardUtils {
    public static boolean isShown(Context context) {
        return ((InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE)).isAcceptingText();
    }
}
