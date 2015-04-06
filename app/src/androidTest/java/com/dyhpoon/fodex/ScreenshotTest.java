package com.dyhpoon.fodex;

import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;

import com.dyhpoon.fodex.fodexView.FodexBaseFragment;
import com.robotium.solo.Solo;
import com.squareup.spoon.Spoon;

/**
 * Created by darrenpoon on 2/4/15.
 */
public class ScreenshotTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;
    private FodexBaseFragment fodexFragment;

    public ScreenshotTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment instanceof FodexBaseFragment) {
            fodexFragment = (FodexBaseFragment) fragment;
            fodexFragment.injectMock(true);
            scrollDown();   // refresh
            solo.sleep(3000);
        } else {
            throw new RuntimeException("Invalid fragment, expecting fragment to be an instance of FodexBaseFragment");
        }
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testScreenshotMain() {
        Spoon.screenshot(solo.getCurrentActivity(), "main");
        solo.sleep(1000);
        solo.clickLongInList(0);
        Spoon.screenshot(solo.getCurrentActivity(), "main_dialog");
    }

    public void testScreenshotNavigation() {
        Point deviceSize = new Point();
        solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getSize(deviceSize);

        int screenWidth = deviceSize.x;
        int screenHeight = deviceSize.y;
        int fromX = 0;
        int toX = screenWidth / 2;
        int fromY = screenHeight / 2;
        int toY = fromY;

        solo.drag(fromX, toX, fromY, toY, 1);
        Spoon.screenshot(solo.getCurrentActivity(), "navigation");
    }

    public void testScreenshotFullscreen() {
        gotoFullscreen();
        Spoon.screenshot(solo.getCurrentActivity(), "fullscreen");
    }

    public void testScreenshotFullscreenShare() {
        gotoFullscreen();

        Display display = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        solo.clickLongOnScreen(width/2, height/2);

        Spoon.screenshot(solo.getCurrentActivity(), "fullscreen_share");
    }

    private void scrollDown() {
        Display display = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        solo.drag(width/2, width/2, height/2 - 50, height - 20, 10);
    }

    private void gotoFullscreen() {
        solo.clickInList(0);
        solo.sleep(1000);
    }

}
