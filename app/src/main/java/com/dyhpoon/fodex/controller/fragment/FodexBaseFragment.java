package com.dyhpoon.fodex.controller.fragment;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.model.MediaPhotoItem;
import com.dyhpoon.fodex.view.ImageGridItem;
import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by darrenpoon on 16/1/15.
 */
public abstract class FodexBaseFragment extends Fragment {

    private AsymmetricGridView mGridView;
    private FloatingActionButton mFloatingActionButton;

    private static final int GRID_VIEW_HORIZONTAL_SPACING = 2;
    private static final int GRID_VIEW_COLUMNS_COUNT = 3;

    /**
     * Triggers when user clicks on the floating action button.
     */
    protected abstract void onClickFloatingActionButton();

    /**
     * Image uri of the photo at #position to be displayed.
     * @param position
     * @param item
     * @return
     */
    protected abstract Uri imageUriForItems(int position, MediaPhotoItem item);

    /**
     * List of media photo items.
     * @return
     */
    protected abstract List<MediaPhotoItem>itemsForAdapters();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fodex_base, container, false);

        mGridView = (AsymmetricGridView) view.findViewById(R.id.grid_view);
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_button);

        setupAsymmetricGridView();
        setupFloatingActionButton();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presentContent();
    }

    private void presentContent() {
        List<MediaPhotoItem>items = itemsForAdapters();
        ((AsymmetricGridViewAdapter)mGridView.getAdapter()).appendItems(items);
    }

    private void setupAsymmetricGridView() {
        mGridView.setRequestedColumnCount(GRID_VIEW_COLUMNS_COUNT);
        mGridView.setRequestedHorizontalSpacing(Utils.dpToPx(getActivity(), GRID_VIEW_HORIZONTAL_SPACING));
        mGridView.setAdapter(new AsymmetricGridViewAdapter<MediaPhotoItem>(getActivity(), mGridView, new ArrayList<MediaPhotoItem>()) {
            @Override
            public View getActualView(int position, View convertView, ViewGroup parent) {
                ImageGridItem gridItem;
                MediaPhotoItem item = getItem(position);
                if (convertView == null) {
                    gridItem = new ImageGridItem(getActivity());
                } else {
                    gridItem = (ImageGridItem) convertView;
                }
                Uri uri = imageUriForItems(position, item);
                gridItem.loadImage(uri);
                return gridItem;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                return view;
            }
        });
    }

    private void setupFloatingActionButton() {
        mFloatingActionButton.attachToListView(mGridView);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFloatingActionButton();
            }
        });
    }

}
