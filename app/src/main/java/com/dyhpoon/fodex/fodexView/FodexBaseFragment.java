package com.dyhpoon.fodex.fodexView;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.Priority;
import com.dyhpoon.fab.FloatingActionsMenu;
import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.data.FodexImageContract;
import com.dyhpoon.fodex.data.FodexItem;
import com.dyhpoon.fodex.fullscreen.FullscreenActivity;
import com.dyhpoon.fodex.view.ImageGridItem;
import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;
import com.felipecsl.asymmetricgridview.library.widget.GridItemViewInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by darrenpoon on 16/1/15.
 */
public abstract class FodexBaseFragment <T extends FodexItem> extends Fragment {

    public State state = State.NORMAL;

    public enum State { NORMAL, SELECTED; }

    private AsymmetricGridView mGridView;
    private FloatingActionsMenu mFloatingActionMenu;

    private static final int GRID_VIEW_HORIZONTAL_SPACING = 3;
    private static final int GRID_VIEW_COLUMNS_COUNT = 3;
    private static final int PRELOAD_SIZE = 10;

    private FodexAdapter mAdapter;
    private DrawableRequestBuilder<Uri> mPreloadRequest;
    private Set<FodexLayoutSpecItem> mSelectedItems = new HashSet<>();
    private List<T> mFodexItems;

    /**
     * Triggers when user clicks on the floating action button.
     */
    protected abstract void onClickConfirmedButton(List<T> selectedItems);

    /**
     * Image uri of the photo at #position to be displayed.
     * @param item
     * @return
     */
    protected abstract Uri imageUriForItems(T item);

    /**
     * List of media photo items.
     * @return
     */
    protected abstract List<T>itemsForAdapters();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fodex_base, container, false);

        mGridView = (AsymmetricGridView) view.findViewById(R.id.grid_view);
        mFloatingActionMenu = (FloatingActionsMenu) view.findViewById(R.id.floating_menu);

        setupFloatingActionButton();
        setupAsymmetricGridView();
        setupPreload();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void reload() {
        mFodexItems = itemsForAdapters();
        if (mFodexItems == null) throw new AssertionError("expect itemsForAdapters to be not null.");

        List<FodexLayoutSpecItem> layoutItems = new ArrayList<FodexLayoutSpecItem>();
        for (int i = 0; i < mFodexItems.size(); i++) {
            int index = i % 10;
            int columnSpan;
            switch (index) {
                case 0:
                case 6:
                    columnSpan = 2;
                    break;
                default:
                    columnSpan = 1;
                    break;
            }
            FodexLayoutSpecItem item = new FodexLayoutSpecItem(columnSpan, 1, mFodexItems.get(i));
            item.uri = imageUriForItems((T) item.object);
            layoutItems.add(i, item);
        }
        ((AsymmetricGridViewAdapter)mGridView.getAdapter()).setItems(layoutItems);
    }

    private void setupFloatingActionButton() {
        mFloatingActionMenu.setOnMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                state = State.SELECTED;
            }

            @Override
            public void onMenuCollapsed() {
                state = State.NORMAL;
                mSelectedItems.clear();
                List<View> visibleViews =
                        ((AsymmetricGridViewAdapter)mGridView.getAdapter()).getVisibleViews();
                for (View view: visibleViews) {
                    view.setSelected(false);
                }
            }
        });
    }

    private void setupAsymmetricGridView() {
        mAdapter = new FodexAdapter(getActivity(), mGridView, new ArrayList<FodexLayoutSpecItem>());

        mGridView.setRequestedColumnCount(GRID_VIEW_COLUMNS_COUNT);
        mGridView.setRequestedHorizontalSpacing(Utils.dpToPx(getActivity(), GRID_VIEW_HORIZONTAL_SPACING));
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(imageOnClickListener);
    }

    private void setupPreload() {
        mPreloadRequest = Glide.with(this)
                .fromMediaStore()
                .fitCenter();

        final AsymmetricSizeProvider sizeProvider =
                new AsymmetricSizeProvider(mAdapter);
        final ListPreloader<FodexLayoutSpecItem> preloader =
                new ListPreloader<FodexLayoutSpecItem>(mAdapter, sizeProvider, PRELOAD_SIZE);
        mFloatingActionMenu.attachToListView(mGridView, null, preloader);
    }

    private AdapterView.OnItemClickListener imageOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
            FodexLayoutSpecItem item = (FodexLayoutSpecItem) mGridView.getItemAtPosition(position);
            ImageGridItem itemView = (ImageGridItem) view;

            if (state == State.SELECTED) {
                setSelectedItem(item, itemView);
            } else {
                startFullscreen(position, item, itemView.imageView);
            }
        }

        private void setSelectedItem(FodexLayoutSpecItem item, ImageGridItem itemView) {
            itemView.setSelected(!itemView.isSelected());
            if (itemView.isSelected()) {
                mSelectedItems.add(item);
            } else {
                mSelectedItems.remove(item);
            }
        }

        private void startFullscreen(int position, FodexLayoutSpecItem item, View view) {
            Intent fullscreenIntent = new Intent(getActivity(), FullscreenActivity.class);
            int[] screenLocation = new int[2];
            view.getLocationOnScreen(screenLocation);

            List<GridItemViewInfo> infos =
                    ((AsymmetricGridViewAdapter)mGridView.getAdapter()).getVisibleViewsInfo();

            fullscreenIntent
                    .putExtra(FullscreenActivity.RESOURCE_INDEX, position)
                    .putExtra(FullscreenActivity.RESOURCE_URL, item.uri.toString())
                    .putExtra(FullscreenActivity.TOP, screenLocation[1])
                    .putExtra(FullscreenActivity.LEFT, screenLocation[0])
                    .putExtra(FullscreenActivity.WIDTH, view.getWidth())
                    .putExtra(FullscreenActivity.HEIGHT, view.getHeight())

                    .putParcelableArrayListExtra(FullscreenActivity.VIEWS_INFO,
                            (ArrayList<? extends android.os.Parcelable>) infos)
                    .putParcelableArrayListExtra(FullscreenActivity.ITEMS_INFO,
                            (ArrayList<? extends android.os.Parcelable>) mFodexItems)

                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            startActivity(fullscreenIntent);

            // Override transitions: we don't want the normal window animation in addition
            // to our custom one
            getActivity().overridePendingTransition(0, 0);
        }
    };

    private class FodexAdapter
            extends AsymmetricGridViewAdapter <FodexLayoutSpecItem>
            implements ListPreloader.PreloadModelProvider <FodexLayoutSpecItem> {

        public FodexAdapter(Context context, AsymmetricGridView listView, List items) {
            super(context, listView, items);
        }

        @Override
        public View getActualView(int position, View convertView, ViewGroup parent) {
            final ImageGridItem gridItem;
            final FodexLayoutSpecItem item = getItem(position);
            if (convertView == null) {
                gridItem = new ImageGridItem(getActivity());
            } else {
                gridItem = (ImageGridItem) convertView;
            }
            gridItem.setSelected(mSelectedItems.contains(item));

            mPreloadRequest
                    .load(item.uri)
                    .priority(Priority.HIGH)
                    .fitCenter()
                    .placeholder(gridItem.colorDrawable)
                    .into(gridItem.imageView);

            return gridItem;
        }

        @Override
        public List<FodexLayoutSpecItem> getPreloadItems(int position) {
            return getRowInfo(position).getItems();
        }

        @Override
        public GenericRequestBuilder getPreloadRequestBuilder(FodexLayoutSpecItem item) {
            return mPreloadRequest.load(item.uri);
        }
    }

    private class AsymmetricSizeProvider extends FodexPreloadSizeProvider <FodexLayoutSpecItem> {

        private FodexAdapter mAdapter;

        public AsymmetricSizeProvider(@NonNull FodexAdapter adapter) {
            this.mAdapter = adapter;
        }

        @Override
        public int[] getSize(FodexLayoutSpecItem item) {
            return new int[]{
                    mAdapter.getRowWidth(item) - FodexImageContract.LEFT_MARGIN - FodexImageContract.RIGHT_MARGIN,
                    mAdapter.getRowHeight(item) - FodexImageContract.TOP_MARGIN - FodexImageContract.BOTTOM_MARGIN};
        }
    }

}
