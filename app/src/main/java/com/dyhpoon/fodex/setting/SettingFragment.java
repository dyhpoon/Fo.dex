package com.dyhpoon.fodex.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dyhpoon.fodex.R;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.gc.materialdesign.views.Switch;

import java.util.Arrays;

/**
 * Created by darrenpoon on 16/3/15.
 */
public class SettingFragment extends Fragment {

    private Switch mFacebookSwitch;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (state.isOpened()) {
                mFacebookSwitch.setChecked(true);
            } else if (state.isClosed()) {
                mFacebookSwitch.setChecked(false);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        mFacebookSwitch = (Switch) view.findViewById(R.id.facebook_switch);
        mFacebookSwitch.setOncheckListener(new Switch.OnCheckListener() {
            @Override
            public void onCheck(boolean b) {
                if (b) {
                    onCheckSelected();
                } else {
                    onCheckUnselected();
                }
            }
        });

        return view;
    }

    private void onCheckSelected() {
        Session session = Session.getActiveSession();
        if (session == null) {
            session = Session.openActiveSessionFromCache(getActivity());
        }
        if (session != null) {
            if (!session.isOpened() && !session.isClosed()) {
                session.openForRead(new Session.OpenRequest(this)
                        .setPermissions(Arrays.asList("public_profile"))
                        .setCallback(statusCallback));
            } else {
                Session.openActiveSession(getActivity(), this, true, Arrays.asList("public_profile"), statusCallback);
            }
        }
    }

    private void onCheckUnselected() {
        Session session = Session.getActiveSession();
        if (session != null) {
            session.closeAndClearTokenInformation();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), statusCallback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
}
