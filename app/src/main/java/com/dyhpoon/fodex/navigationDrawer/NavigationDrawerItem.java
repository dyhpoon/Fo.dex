package com.dyhpoon.fodex.navigationDrawer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dyhpoon.fodex.R;

/**
 * Created by darrenpoon on 22/1/15.
 */
public class NavigationDrawerItem extends LinearLayout implements Checkable {

    public ImageView iconImageView;
    public TextView titleTextView;
    private boolean mChecked = false;

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
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked == checked) return;

        int color = (checked)
                ? getResources().getColor(R.color.colorPrimary)
                : getResources().getColor(R.color.colorPrimaryText);
        iconImageView.setColorFilter(color);
        titleTextView.setTextColor(color);

        mChecked = checked;
    }

    // for details, see http://stackoverflow.com/questions/3742979/how-to-get-a-android-listview-item-selector-to-use-state-checked
    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace);
        setChecked(mChecked);
        return drawableState;
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }
}
