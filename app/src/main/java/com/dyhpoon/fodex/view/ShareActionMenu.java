package com.dyhpoon.fodex.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.dyhpoon.fodex.R;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.oguzdev.circularfloatingactionmenu.library.animation.BouncyAnimationHandler;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by darrenpoon on 13/2/15.
 */
public class ShareActionMenu {

    private FloatingActionButton mFabButton;
    private FloatingActionMenu mMenu;

    public ShareActionMenu(Context context, int position) {
        Resources resources = context.getResources();

        // Floating Action Button - cross button
        int size = resources.getDimensionPixelSize(R.dimen.fullscreen_action_menu_size);
        mFabButton = new FloatingActionButton.Builder(context)
                .setContentView(null, new FloatingActionButton.LayoutParams(size, size))
                .setBackgroundDrawable(R.drawable.circle_ring)
                .setPosition(position)
                .setLayoutParams(new FloatingActionButton.LayoutParams(size, size))
                .build();
        mFabButton.setEnabled(false);
        mFabButton.setVisibility(View.INVISIBLE);

        // Sub Action Buttons
        int subSize = resources.getDimensionPixelSize(R.dimen.fullscreen_sab_button_size);
        int borderWidth = resources.getDimensionPixelSize(R.dimen.fullscreen_sab_button_border_width);
        SubActionButton.Builder buttonsBuilder = new SubActionButton.Builder(context)
                .setLayoutParams(new FrameLayout.LayoutParams(subSize, subSize));
        // - Facebook
        CircleImageView facebookIcon =
                createCircleButton(context, R.drawable.ic_facebook, subSize, borderWidth);
        // - WhatsApp
        CircleImageView whatsappIcon =
                createCircleButton(context, R.drawable.ic_whatsapp, subSize, borderWidth);
        // - Google+
        CircleImageView googlePlusIcon =
                createCircleButton(context, R.drawable.ic_googleplus, subSize, borderWidth);

        // setup menu
        mMenu = new FloatingActionMenu.Builder(context)
                .addSubActionView(buttonsBuilder.setContentView(facebookIcon).build())
                .addSubActionView(buttonsBuilder.setContentView(whatsappIcon).build())
                .addSubActionView(buttonsBuilder.setContentView(googlePlusIcon).build())
                .setAnimationHandler(new BouncyAnimationHandler())
                .setStartAngle(-25)
                .setEndAngle(-150)
                .attachTo(mFabButton)
                .build();

        // override original onClickListener, we will open/close it in other ways
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }

    public void open() {
        if (!mMenu.isOpen()) {
            mFabButton.setVisibility(View.VISIBLE);
            mFabButton.setEnabled(true);
            mMenu.open(true);
        }
    }

    public void close() {
        if (mMenu.isOpen()) {
            mMenu.close(true);
            mFabButton.setEnabled(false);
            mFabButton.setVisibility(View.INVISIBLE);
        }
    }

    private CircleImageView createCircleButton(Context context, int resId, int size, int borderWidth) {
        CircleImageView circleIcon = new CircleImageView(context);
        circleIcon.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        circleIcon.setImageDrawable(context.getResources().getDrawable(resId));
        circleIcon.setBorderWidth(borderWidth);
        circleIcon.setBorderColor(Color.WHITE);
        return circleIcon;
    }

}
