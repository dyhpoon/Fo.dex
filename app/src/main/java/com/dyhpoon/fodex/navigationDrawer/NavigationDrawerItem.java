package com.dyhpoon.fodex.navigationDrawer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dyhpoon.fodex.R;

/**
 * Created by darrenpoon on 22/1/15.
 */
public class NavigationDrawerItem extends LinearLayout {

    public ImageView iconImageView;
    public TextView titleTextView;
    private State mState = State.UNSELECTED;

    public enum State {
        SELECTED, UNSELECTED,
    }

    public NavigationDrawerItem(Context context) {
        super(context);
        setup();
    }

    public NavigationDrawerItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public NavigationDrawerItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        inflate(getContext(), R.layout.list_item_navigation_drawer, this);

        // add attributes here, since layout.xml is using 'merge' as the root element
        // see http://trickyandroid.com/protip-inflating-layout-for-your-custom-view/
        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.navigation_drawer_list_item_height)
        ));
        setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.navigation_drawer_list_item_height));
        setOrientation(LinearLayout.HORIZONTAL);
        setVerticalGravity(Gravity.CENTER_VERTICAL);

        int color = getResources().getColor(R.color.colorPrimaryText);
        iconImageView = (ImageView) findViewById(R.id.icon);
        iconImageView.setColorFilter(color);
        titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setTextColor(color);

        setState(mState);
    }

    public void setState(State state) {
        if (state == mState) return;

        int color = (state == State.SELECTED)
                ? getResources().getColor(R.color.colorPrimaryText)
                : getResources().getColor(R.color.colorPrimary);
        iconImageView.setColorFilter(color);
        titleTextView.setTextColor(color);

        mState = state;
    }

}
