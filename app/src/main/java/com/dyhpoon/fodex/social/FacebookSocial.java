package com.dyhpoon.fodex.social;

import android.app.Activity;
import android.util.Log;

import com.dyhpoon.fodex.util.OnCompleteListener;
import com.facebook.Session;
import com.facebook.SessionState;

import java.util.Arrays;
import java.util.List;

/**
 * Created by darrenpoon on 17/3/15.
 */
public class FacebookSocial implements Social {

    @Override
    public boolean isActive() {
        Session session = Session.getActiveSession();
        return (session != null && session.isOpened());
    }

    @Override
    public void login(Activity activity, OnCompleteListener listener) {
        Session session = Session.getActiveSession();
        Session.StatusCallback callback = new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (state.isOpened()) {
                    Log.v("HELLO", "Logged in...");
                } else if (state.isClosed()) {
                    Log.v("HELLO", "Logged out...");
                }
            }
        };
        List<String> permissions = Arrays.asList("public_profile");
        if (session != null) {
            if (!session.isOpened() && !session.isClosed()) {
                session.openForRead(new Session.OpenRequest(activity)
                        .setPermissions(permissions)
                        .setCallback(callback));
            } else {
                Session.openActiveSession(activity, false, permissions, callback);
            }
        }
    }
}
