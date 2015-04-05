package com.dyhpoon.fodex.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.util.Attributes;
import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.util.SimpleNineoldAnimatorListener;
import com.nineoldandroids.animation.Animator;

import java.util.List;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

/**
 * Created by darrenpoon on 27/2/15.
 */
public class ShowTagsDialog extends SupportBlurDialogFragment {

    public interface OnDeleteListener {
        public void onDelete(CharSequence tag);
    }

    private List<String> mTags;
    private RecyclerView mRecyclerView;
    private DialogInterface.OnClickListener mListener;
    private OnDeleteListener mDeleteListener;

    public ShowTagsDialog(List<String> tags) {
        mTags = tags;
    }

    public static ShowTagsDialog newInstance(List<String> tags) {
        ShowTagsDialog dialog = new ShowTagsDialog(tags);
        return dialog;
    }

    public void setOnClickListener(DialogInterface.OnClickListener listener) {
        mListener = listener;
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        mDeleteListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_show_tag, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        // setup recycler view
        int itemMargin = getActivity().getResources().getDimensionPixelSize(R.dimen.dialog_item_margin);
        TagAdapter adapter = new TagAdapter(getActivity(), mTags);
        adapter.setMode(Attributes.Mode.Single);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(itemMargin));
        mRecyclerView.setAdapter(adapter);

        // setup animation
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // setup button's onClickListener
        Button closeButton = (Button) view.findViewById(R.id.button_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
            }
        });

        return dialog;
    }

    @Override
    protected boolean isDebugEnable() {
        return false;
    }

    @Override
    protected boolean isDimmingEnable() {
        return false;
    }

    @Override
    protected boolean isActionBarBlurred() {
        return true;
    }

    @Override
    protected float getDownScaleFactor() {
        return 8f;
    }

    @Override
    protected int getBlurRadius() {
        return 8;
    }

    @Override
    protected boolean isRenderScriptEnable() {
        return true;
    }

    private class TagAdapter extends RecyclerSwipeAdapter<TagAdapter.TagViewHolder> {

        private Context mContext;
        private List<String> mTags;

        public TagAdapter(Context context, List<String> tags) {
            mContext = context;
            mTags = tags;
        }

        @Override
        public int getSwipeLayoutResourceId(int i) {
            return R.id.swipe;
        }

        @Override
        public TagAdapter.TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_removeable_item, parent, false);

            // setup button
            final TagAdapter.TagViewHolder vh = new TagViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final TagViewHolder viewHolder, final int position) {
            viewHolder.textView.setText(mTags.get(position));
            viewHolder.deleteButton.setEnabled(true);
            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolder.deleteButton.setEnabled(false);
                    // animate and delete tag
                    YoYo.with(Techniques.Bounce).duration(200).withListener(new SimpleNineoldAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            String tag = viewHolder.textView.getText().toString();
                            mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                            mTags.remove(tag);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, mTags.size());
                            mItemManger.closeAllItems();
                            mDeleteListener.onDelete(tag);
                        }
                    }).playOn(viewHolder.deleteButton);
                }
            });
            viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                @Override
                public void onOpen(SwipeLayout layout) {
                    YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash_image_view));
                }
            });
            mItemManger.bindView(viewHolder.itemView, position);
        }

        @Override
        public int getItemCount() {
            return mTags.size();
        }

        public class TagViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            SwipeLayout swipeLayout;
            Button deleteButton;

            public TagViewHolder(View view) {
                super(view);
                textView = (TextView) view.findViewById(R.id.text_view);
                swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe);
                deleteButton = (Button) view.findViewById(R.id.button_delete);
            }
        }
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }
}
