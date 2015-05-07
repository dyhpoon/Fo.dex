package com.dyhpoon.fodex.fodexView;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.data.FodexCore;
import com.dyhpoon.fodex.util.OnCompleteListener;
import com.dyhpoon.fodex.view.ErrorToast;
import com.dyhpoon.fodex.view.InsertTagDialog;
import com.dyhpoon.fodex.view.InsertTagToast;
import com.dyhpoon.fodex.view.PleaseInertTagToast;
import com.dyhpoon.fodex.view.ShowTagsDialog;

import java.util.List;

/**
 * Created by darrenpoon on 22/3/15.
 */
public class FodexWidget {

    private static final String DIALOG_TAG = "fodex_dialog";

    public static void showTags(final FragmentActivity activity, final FodexCore fodexCore, final long fodexId) {
        FragmentManager manager = activity.getSupportFragmentManager();

        // do nothing, if manager is null or a dialog is already shown
        if (manager == null || manager.findFragmentByTag(DIALOG_TAG) != null) {
            return;
        }

        List<String> tags = fodexCore.getTags(activity, fodexId);
        if (tags.size() > 0) {
            final ShowTagsDialog dialog = ShowTagsDialog.newInstance(tags);
            dialog.setOnClickListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.setOnDeleteListener(new ShowTagsDialog.OnDeleteListener() {
                @Override
                public void onDelete(CharSequence tag) {
                    fodexCore.deleteTagFromPhoto(activity, fodexId, tag.toString());
                    // close dialog if the last tag is deleted
                    if (fodexCore.getTags(activity, fodexId).size() == 0) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.show(manager, DIALOG_TAG);
        } else {
            ErrorToast.make(activity, activity.getString(R.string.message_no_tag)).show();
        }
    }

    public static void addTags(final FragmentActivity activity, final FodexCore fodexCore, final long[] imageIds, final OnCompleteListener listener) {
        FragmentManager manager = activity.getSupportFragmentManager();

        // do nothing, if manager is null or a dialog is already shown
        if (manager == null || manager.findFragmentByTag(DIALOG_TAG) != null) {
            return;
        }

        InsertTagDialog dialog = InsertTagDialog.newInstance(imageIds.length);
        dialog.setOnClickListener(new InsertTagDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, String[] tags, int which) {
                switch (which) {
                    // click add
                    case DialogInterface.BUTTON_POSITIVE:
                        if (tags != null) {
                            // add tags to DB
                            fodexCore.addTagsToPhotos(activity, imageIds, tags);

                            // cleanup and show success message
                            dialog.dismiss();
                            InsertTagToast.make(activity, imageIds.length).show();
                            if (listener != null)
                                listener.didComplete();
                        } else {
                            // show error
                            PleaseInertTagToast.make(activity).show();
                            if (listener != null)
                                listener.didFail();
                        }
                        break;
                    // click cancel
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        });
        dialog.show(manager, DIALOG_TAG);
    }
}
