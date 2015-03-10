package com.dyhpoon.fodex.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.util.StringUtils;

import java.util.List;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

/**
 * Created by darrenpoon on 20/2/15.
 */
public class InsertTagDialog extends SupportBlurDialogFragment {

    private EditText mEditText;
    private OnClickListener mListener;
    private int mNumberOfPhotos = 0;

    public interface OnClickListener {
        public void onClick(DialogInterface dialog, String[] tags, int which);
    }

    private InsertTagDialog(int numberOfPhotos) {
        this.mNumberOfPhotos = numberOfPhotos;
    }

    public static InsertTagDialog newInstance(int numberOfPhotos) {
        InsertTagDialog dialog = new InsertTagDialog(numberOfPhotos);
        return dialog;
    }

    public void setOnClickListener(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_insert_tag, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        // setup title
        TextView tv = (TextView) view.findViewById(R.id.text_view);
        tv.setText(getActivity().getResources().getQuantityString(R.plurals.dialog_index_photo, mNumberOfPhotos));

        // setup editText
        mEditText = (EditText) view.findViewById(R.id.edit_text);

        // setup animation
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // setup buttons' onClickListener
        Button cancelButton = (Button) view.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new InsertTagOnClickListener(dialog, mEditText, DialogInterface.BUTTON_NEGATIVE, mListener));
        Button addButton = (Button) view.findViewById(R.id.button_add);
        addButton.setOnClickListener(new InsertTagOnClickListener(dialog, mEditText, DialogInterface.BUTTON_POSITIVE, mListener));
        return dialog;
    }

    private class InsertTagOnClickListener implements View.OnClickListener {
        private Dialog mDialog;
        private EditText mEditText;
        private int mDialogButtonType;
        private OnClickListener mListener;

        public InsertTagOnClickListener(Dialog dialog, EditText editText, int dialogButtonType, OnClickListener listener) {
            mDialog = dialog;
            mEditText = editText;
            mDialogButtonType = dialogButtonType;
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                List<String> filteredTags = StringUtils.tokenize(mEditText.getText().toString());
                if (filteredTags.size() == 0) {
                    filteredTags = null;
                }
                mListener.onClick(
                        mDialog,
                        filteredTags.toArray(new String[filteredTags.size()]),
                        mDialogButtonType);
            }
        }
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

}
