package com.dyhpoon.fodex.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dyhpoon.fodex.R;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

/**
 * Created by darrenpoon on 20/2/15.
 */
public class InsertTagDialog extends SupportBlurDialogFragment {

    private DialogInterface.OnClickListener mListener;

    public static InsertTagDialog newInstance() {
        InsertTagDialog dialog = new InsertTagDialog();
        return dialog;
    }

    public void setOnClickListener(DialogInterface.OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_insert_tag, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        Button cancelButton = (Button) view.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
            }
        });
        Button addButton = (Button) view.findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
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

}
