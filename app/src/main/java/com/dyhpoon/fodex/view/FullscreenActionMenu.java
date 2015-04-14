package com.dyhpoon.fodex.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dyhpoon.fodex.R;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.OnBounceListener;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.oguzdev.circularfloatingactionmenu.library.animation.BouncyAnimationHandler;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by darrenpoon on 13/2/15.
 */
public class FullscreenActionMenu {

    public enum ActionType {
        GOOGLE,
        FACEBOOK,
        WHATSAPP,
        INSTAGRAM,
        SHOWTAGS,
        ADDTAGS,
    }

    public interface OnClickListener {
        public void onClick(ActionType type);
    }

    private OnClickListener mListener;
    private FloatingActionButton mFabButton;
    private FloatingActionMenu mMenu;

    public FullscreenActionMenu(Context context, int position) {
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
                createCircleButton(context, R.drawable.ic_facebook_60, borderWidth);
        // - WhatsApp
        CircleImageView whatsappIcon =
                createCircleButton(context, R.drawable.ic_whatsapp_60, borderWidth);
        // - Google+
        CircleImageView googlePlusIcon =
                createCircleButton(context, R.drawable.ic_googleplus_60, borderWidth);
        // - Instagram
        CircleImageView instagramIcon =
                createCircleButton(context, R.drawable.ic_instagram_60, borderWidth);
        // - Show Tags
        CircleImageView showTagsIcon =
                createCircleButton(context, R.drawable.ic_tag_60_orange, borderWidth);
        // - Add tags
        CircleImageView addTagsIcon =
                createCircleButton(context, R.drawable.ic_add_60_purple, borderWidth);

        // setup menu
        mMenu = new FloatingActionMenu.Builder(context)
                .addSubActionView(buttonsBuilder
                        .setContentView(instagramIcon)
                        .setBounceListener(new OnBounceListener() {
                            @Override
                            public void onBounce() {
                                if (mListener != null)
                                    mListener.onClick(ActionType.INSTAGRAM);
                            }
                        }).build())
                .addSubActionView(buttonsBuilder
                        .setContentView(whatsappIcon)
                        .setBounceListener(new OnBounceListener() {
                            @Override
                            public void onBounce() {
                                if (mListener != null)
                                    mListener.onClick(ActionType.WHATSAPP);
                            }
                        }).build())
                .addSubActionView(buttonsBuilder.setContentView(showTagsIcon)
                        .setBounceListener(new OnBounceListener() {
                            @Override
                            public void onBounce() {
                                if (mListener != null)
                                    mListener.onClick(ActionType.SHOWTAGS);
                            }
                        }).build())
                .addSubActionView(buttonsBuilder.setContentView(addTagsIcon)
                        .setBounceListener(new OnBounceListener() {
                            @Override
                            public void onBounce() {
                                if (mListener != null)
                                    mListener.onClick(ActionType.ADDTAGS);
                            }
                        }).build())
                .addSubActionView(buttonsBuilder
                        .setContentView(googlePlusIcon)
                        .setBounceListener(new OnBounceListener() {
                            @Override
                            public void onBounce() {
                                if (mListener != null)
                                    mListener.onClick(ActionType.GOOGLE);
                            }
                        }).build())
                .addSubActionView(buttonsBuilder
                        .setContentView(facebookIcon)
                        .setBounceListener(new OnBounceListener() {
                            @Override
                            public void onBounce() {
                                if (mListener != null)
                                    mListener.onClick(ActionType.FACEBOOK);
                            }
                        }).build())
                .setAnimationHandler(new BouncyAnimationHandler())
                .setStartAngle(240)
                .setEndAngle(-60)
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

    public boolean isOpen() {
        return mMenu.isOpen();
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

    private CircleImageView createCircleButton(Context context, int resId, int borderWidth) {
        CircleImageView circleIcon = new CircleImageView(context);
        circleIcon.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        circleIcon.setImageDrawable(ContextCompat.getDrawable(context, resId));
        circleIcon.setBorderWidth(borderWidth);
        circleIcon.setBorderColor(Color.WHITE);
        return circleIcon;
    }

    public void setOnClickListener(OnClickListener listener) {
        mListener = listener;
    }

}
