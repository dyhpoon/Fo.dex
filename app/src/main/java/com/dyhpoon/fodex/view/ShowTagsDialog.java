package com.dyhpoon.fodex.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.daimajia.swipe.util.Attributes;
import com.dyhpoon.fodex.R;

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
    private ListView mListView;
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

        // setup listview
        mListView = (ListView) view.findViewById(R.id.list_view);
        mListView.setAdapter(new TagAdapter(getActivity(), mTags));

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

    private class TagAdapter extends BaseSwipeAdapter {

        private Context mContext;
        private List<String> mTags;

        public TagAdapter(Context context, List<String>tags) {
            mContext = context;
            mTags = tags;
            setMode(Attributes.Mode.Single);
        }

        @Override
        public int getSwipeLayoutResourceId(int i) {
            return R.id.swipe;
        }

        @Override
        public View generateView(int i, ViewGroup viewGroup) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_removeable_item, null);

            // tag textview
            final TextView tv = (TextView) view.findViewById(R.id.text_view);
            view.setTag(new ViewHolder(tv));

            // delete button
            final Button deleteButton = (Button) view.findViewById(R.id.button_delete);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDeleteListener.onDelete(tv.getText());
                }
            });
            return view;
        }

        @Override
        public void fillValues(int i, View view) {
            ViewHolder vh = (ViewHolder) view.getTag();
            vh.textView.setText(mTags.get(i));
        }

        @Override
        public int getCount() {
            return mTags.size();
        }

        @Override
        public Object getItem(int position) {
            return mTags.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView textView;

            public ViewHolder(TextView view) {
                textView = view;
            }
        }
    }
}
