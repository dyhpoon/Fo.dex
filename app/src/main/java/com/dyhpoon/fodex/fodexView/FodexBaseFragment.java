package com.dyhpoon.fodex.fodexView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dyhpoon.fab.FloatingActionButton;
import com.dyhpoon.fab.FloatingActionsMenu;
import com.dyhpoon.fodex.MainActivity;
import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.data.FodexCore;
import com.dyhpoon.fodex.data.FodexItem;
import com.dyhpoon.fodex.data.actual.FodexContract;
import com.dyhpoon.fodex.di.BaseFragment;
import com.dyhpoon.fodex.fullscreen.FullscreenActivity;
import com.dyhpoon.fodex.util.CacheImageManager;
import com.dyhpoon.fodex.util.KeyboardUtils;
import com.dyhpoon.fodex.util.SimpleCompleteListener;
import com.dyhpoon.fodex.util.StringUtils;
import com.dyhpoon.fodex.view.ErrorToast;
import com.dyhpoon.fodex.view.ImageGridItem;
import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;


/**
 * Created by darrenpoon on 16/1/15.
 */
public abstract class FodexBaseFragment<T extends FodexItem>
        extends BaseFragment
        implements OnBackPressedHandler {

    private enum State {
        BROWSE,
        SELECTING_PHOTOS,
        SEARCHING_PHOTOS,
        SELECTING_FILTERED_PHOTOS,
    }

    private static final int GRID_VIEW_HORIZONTAL_SPACING = 3;
    private static final int GRID_VIEW_COLUMNS_COUNT = 3;
    private static final int PRELOAD_SIZE = 10;

    @Inject FodexCore fodexCore;

    private SearchView mSearchView;
    private AsymmetricGridView mGridView;
    private FloatingActionButton mTagButton;
    private FloatingActionsMenu mFloatingActionMenu;
    private PtrClassicFrameLayout mPtrLayout;

    private FodexAdapter mAdapter;
    private Cursor mSuggestionCursor;
    private DrawableRequestBuilder<Uri> mPreloadRequest;
    private List<T> mFodexItems;
    private List<FodexLayoutSpecItem> mSelectedItems = new ArrayList<>();

    /**
     * List of media photo items.
     *
     * @return
     */
    protected abstract List<T> itemsForAdapters();

    /**
     * Called when user submits a query in searchView.
     *
     * @param tags
     */
    protected abstract void onQueryTagsSubmitted(List<String> tags);

    /**
     * Called when user clicks on the cross button in searchView
     */
    protected abstract void onSearchEnd();

    /**
     * Called when refresh has begun
     */
    protected abstract void onRefreshBegin();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fodex_base, container, false);

        mGridView = (AsymmetricGridView) view.findViewById(R.id.grid_view);
        mFloatingActionMenu = (FloatingActionsMenu) view.findViewById(R.id.floating_menu);
        mTagButton = (FloatingActionButton) view.findViewById(R.id.floating_tag_button);
        mPtrLayout = (PtrClassicFrameLayout) view.findViewById(R.id.swipe_container);

        setupFloatingButtonsAndMenu();
        setupAsymmetricGridView();
        setupPullToRefresh();
        setupImagePreload();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // make sure drawer is unlock
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).drawerLayout
                    .setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CacheImageManager.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSuggestionCursor != null) {
            mSuggestionCursor.close();
        }
    }

    @Override
    public boolean onCustomBackPressed() {
        State currentState = getState();
        if (currentState == State.SEARCHING_PHOTOS) {
            mSearchView.setIconified(true);
            return true;
        } else if (currentState == State.SELECTING_FILTERED_PHOTOS ||
                currentState == State.SELECTING_PHOTOS) {
            mFloatingActionMenu.collapse();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.main, menu);

        // setup SearchView and EditText
        final MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        setupSearchView();
    }

    public void reload() {
        CacheImageManager.clear();
        mFodexItems = itemsForAdapters();
        if (mFodexItems == null)
            throw new AssertionError("expect itemsForAdapters to be not null.");

        List<FodexLayoutSpecItem> layoutItems = new ArrayList<>();
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
            layoutItems.add(i, item);
        }
        ((AsymmetricGridViewAdapter) mGridView.getAdapter()).setItems(layoutItems);
        mFloatingActionMenu.collapse();

        // notify user no photos found
        if (layoutItems.size() == 0) {
            ErrorToast.make(getActivity(), getString(R.string.message_no_photos_found)).show();
        }
    }

    public void refreshComplete() {
        mPtrLayout.post(new Runnable() {
            @Override
            public void run() {
                mPtrLayout.refreshComplete();
                mGridView.setAlpha(0.f);
                reload();
                YoYo.with(Techniques.FadeIn).duration(800).playOn(mGridView);
            }
        });
    }

    private void setupFloatingButtonsAndMenu() {
        mFloatingActionMenu.setOnMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
            }

            @Override
            public void onMenuCollapsed() {
                mSelectedItems.clear();
                List<View> visibleViews =
                        ((AsymmetricGridViewAdapter) mGridView.getAdapter()).getVisibleViews();
                for (View view : visibleViews) {
                    view.setSelected(false);
                }
            }
        });
        mFloatingActionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dont toggle menu if keyboard is shown
                if (getState() == State.SEARCHING_PHOTOS && KeyboardUtils.isShown(getActivity())) {
                    mSearchView.setIconified(true);
                } else {
                    mFloatingActionMenu.toggle();
                }
            }
        });
        mTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedItems.size() > 0) {
                    showAddTagDialog();
                } else {
                    ErrorToast.make(getActivity(), getString(R.string.message_no_photo)).show();
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
        mGridView.setOnItemLongClickListener(imageOnLongClickListener);
        mGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (KeyboardUtils.isShown(getActivity())) {
                    mSearchView.clearFocus();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void setupPullToRefresh() {
        mPtrLayout.setLastUpdateTimeRelateObject(this);
        mPtrLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                FodexBaseFragment.this.onRefreshBegin();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });

        Resources res = getActivity().getResources();
        final MaterialHeader header = new MaterialHeader(getActivity());
        final int topPadding = res.getDimensionPixelSize(R.dimen.pulltorefresh_top_padding);
        final int bottomPadding = res.getDimensionPixelSize(R.dimen.pulltorefresh_bottom_padding);
        header.setColorSchemeColors(res.getIntArray(R.array.google_colors));
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, topPadding, 0, bottomPadding);
        header.setPtrFrameLayout(mPtrLayout);
        mPtrLayout.setHeaderView(header);
        mPtrLayout.addPtrUIHandler(header);
    }

    private void setupImagePreload() {
        mPreloadRequest = Glide.with(this)
                .fromMediaStore()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter();

        final AsymmetricSizeProvider sizeProvider = new AsymmetricSizeProvider(mAdapter);
        final ListPreloader<FodexLayoutSpecItem> preloader = new ListPreloader<>(mAdapter, sizeProvider, PRELOAD_SIZE);
        mFloatingActionMenu.attachToListView(mGridView, null, preloader);
    }

    private void setupSearchView() {
        final EditText searchText = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchText.setHint(R.string.search_bar_hint);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                List<String> filteredTags = StringUtils.tokenize(s);
                if (filteredTags.size() != 0) {
                    onQueryTagsSubmitted(filteredTags);
                }
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // close adapter if exists, prevent leak
                if (mSuggestionCursor != null) {
                    mSuggestionCursor.close();
                }

                List<String> filteredTags = StringUtils.tokenize(s);
                if (filteredTags.size() != 0) {
                    String lastTag = filteredTags.remove(filteredTags.size() - 1);
                    mSuggestionCursor = fodexCore.getMatchedTags(getActivity(), lastTag);
                    SearchViewCursorAdapter adapter =
                            new SearchViewCursorAdapter(getActivity(),
                                    mSuggestionCursor,
                                    TextUtils.join(" ", filteredTags));

                    mSearchView.setSuggestionsAdapter(adapter);
                    return true;
                } else {
                    return false;
                }
            }
        });

        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                if (mSuggestionCursor == null) {
                    return false;
                }

                // set suggestion word to edit text
                mSuggestionCursor.moveToPosition(i);
                String suggestion =
                        mSuggestionCursor.getString(mSuggestionCursor.getColumnIndex(FodexContract.TagEntry.COLUMN_TAG_NAME));

                // remove the last element and append suggestion word to it
                List<String> textsInEditText = StringUtils.tokenize(searchText.getText().toString());
                if (textsInEditText.size() > 0) {
                    textsInEditText.remove(textsInEditText.size() - 1);
                }
                textsInEditText.add(suggestion);
                searchText.setText(TextUtils.join(" ", textsInEditText));

                // set edit text selection
                searchText.setSelection(searchText.getText().length());

                return true;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                onSearchEnd();
                return false;
            }
        });

        EditText editText = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mFloatingActionMenu.collapse();
            }
        });
    }

    private void showAddTagDialog() {
        long[] imageIds = new long[mSelectedItems.size()];
        for (int i = 0; i < mSelectedItems.size(); i++) {
            imageIds[i] = mSelectedItems.get(i).fodexItem.id;
        }
        FodexWidget.addTags(getActivity(), fodexCore, imageIds, new SimpleCompleteListener() {
            @Override
            public void didComplete() {
                if (mFloatingActionMenu.isExpanded()) mFloatingActionMenu.collapse();
            }
        });
    }

    private State getState() {
        if (mFloatingActionMenu.isExpanded() && mSearchView != null && !mSearchView.isIconified()) {
            return State.SELECTING_FILTERED_PHOTOS;
        } else if (mFloatingActionMenu.isExpanded()) {
            return State.SELECTING_PHOTOS;
        } else if (mSearchView != null && !mSearchView.isIconified()) {
            return State.SEARCHING_PHOTOS;
        } else {
            return State.BROWSE;
        }
    }

    private AdapterView.OnItemLongClickListener imageOnLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final FodexItem item = ((FodexLayoutSpecItem) mGridView.getItemAtPosition(position)).fodexItem;
            FodexWidget.showTags(getActivity(), fodexCore, item.id);
            return true;
        }
    };

    private AdapterView.OnItemClickListener imageOnClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
            // if keyboard is shown, hide it
            if (KeyboardUtils.isShown(getActivity())) {
                mSearchView.clearFocus();
                return;
            }
            FodexLayoutSpecItem item = (FodexLayoutSpecItem) mGridView.getItemAtPosition(position);
            ImageGridItem itemView = (ImageGridItem) view;

            State currentState = getState();
            if (currentState == State.SELECTING_FILTERED_PHOTOS ||
                    currentState == State.SELECTING_PHOTOS) {
                setSelectedItem(item, itemView);
            } else {
                startFullscreen(position, item, itemView.imageView);
            }
        }

        private void setSelectedItem(FodexLayoutSpecItem item, ImageGridItem itemView) {
            itemView.setSelected(!itemView.isSelected());
            if (itemView.isSelected()) {
                YoYo.with(Techniques.Tada).duration(200).playOn(itemView);
                mSelectedItems.add(item);
            } else {
                YoYo.with(Techniques.Pulse).duration(200).playOn(itemView);
                mSelectedItems.remove(item);
            }
        }

        private void startFullscreen(int position, FodexLayoutSpecItem item, View view) {
            // lock drawer, prevent user to open drawer when transitioning to fullscreen.
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).drawerLayout
                        .setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            Intent fullscreenIntent = new Intent(getActivity(), FullscreenActivity.class);
            int[] screenLocation = new int[2];
            view.getLocationOnScreen(screenLocation);

            fullscreenIntent
                    .putExtra(FullscreenActivity.RESOURCE_INDEX, position)
                    .putExtra(FullscreenActivity.RESOURCE_URL, item.fodexItem.uri.toString())
                    .putExtra(FullscreenActivity.TOP, screenLocation[1])
                    .putExtra(FullscreenActivity.LEFT, screenLocation[0])
                    .putExtra(FullscreenActivity.WIDTH, view.getWidth())
                    .putExtra(FullscreenActivity.HEIGHT, view.getHeight())

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
            extends AsymmetricGridViewAdapter<FodexLayoutSpecItem>
            implements ListPreloader.PreloadModelProvider<FodexLayoutSpecItem> {

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

            final Bitmap bitmap = CacheImageManager.getCache(item.fodexItem.uri);
            if (bitmap != null && !bitmap.isRecycled()) {
                Glide.clear(gridItem.imageView);    // if is loading in glide, cancel it.
                gridItem.imageView.setImageBitmap(bitmap);
            } else {
                mPreloadRequest
                        .load(item.fodexItem.uri)
                        .priority(Priority.HIGH)
                        .fitCenter()
                        .placeholder(gridItem.colorDrawable)
                        .into(new GlideDrawableImageViewTarget(gridItem.imageView) {
                            @Override
                            public void onResourceReady(final GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                super.onResourceReady(resource, animation);
                                CacheImageManager.cacheImage(item.fodexItem.uri, resource);
                            }
                        });
            }

            return gridItem;
        }

        @Override
        public List<FodexLayoutSpecItem> getPreloadItems(int position) {
            return getRowInfo(position).getItems();
        }

        @Override
        public GenericRequestBuilder getPreloadRequestBuilder(FodexLayoutSpecItem item) {
            return mPreloadRequest.load(item.fodexItem.uri);
        }
    }

}
