package com.dyhpoon.fodex.fodexView;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.Priority;
import com.dyhpoon.fab.FloatingActionButton;
import com.dyhpoon.fab.FloatingActionsMenu;
import com.dyhpoon.fodex.R;
import com.dyhpoon.fodex.data.FodexContract;
import com.dyhpoon.fodex.data.FodexCore;
import com.dyhpoon.fodex.data.FodexItem;
import com.dyhpoon.fodex.data.SearchViewCursorAdapter;
import com.dyhpoon.fodex.fullscreen.FullscreenActivity;
import com.dyhpoon.fodex.util.StringUtils;
import com.dyhpoon.fodex.view.ImageGridItem;
import com.dyhpoon.fodex.view.InsertTagDialog;
import com.dyhpoon.fodex.view.InsertTagToast;
import com.dyhpoon.fodex.view.NoPhotoToast;
import com.dyhpoon.fodex.view.NoTagToast;
import com.dyhpoon.fodex.view.PleaseInertTagToast;
import com.dyhpoon.fodex.view.ShowTagsDialog;
import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by darrenpoon on 16/1/15.
 */
public abstract class FodexBaseFragment <T extends FodexItem>
        extends Fragment
        implements OnBackPressedHandler {

    public State state = State.NORMAL;

    public enum State { NORMAL, SELECTED; }

    private AsymmetricGridView mGridView;
    private FloatingActionsMenu mFloatingActionMenu;
    private FloatingActionButton mTagButton;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int GRID_VIEW_HORIZONTAL_SPACING = 3;
    private static final int GRID_VIEW_COLUMNS_COUNT = 3;
    private static final int PRELOAD_SIZE = 10;

    private FodexAdapter mAdapter;
    private DrawableRequestBuilder<Uri> mPreloadRequest;
    private List<FodexLayoutSpecItem> mSelectedItems = new ArrayList<>();
    private List<T> mFodexItems;
    private Cursor mSuggestionCursor;
    private SearchView mSearchView;

    /**
     * List of media photo items.
     * @return
     */
    protected abstract List<T>itemsForAdapters();

    /**
     * Called when user submits a query in searchView.
     * @param tags
     */
    protected abstract void onQueryTagsSubmitted(List<String> tags);

    /**
     * Called when user clicks on the cross button in searchView
     */
    protected abstract void onSearchEnd();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fodex_base, container, false);

        mGridView = (AsymmetricGridView) view.findViewById(R.id.grid_view);
        mFloatingActionMenu = (FloatingActionsMenu) view.findViewById(R.id.floating_menu);
        mTagButton = (FloatingActionButton) view.findViewById(R.id.floating_tag_button);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        setupFloatingButtonsAndMenu();
        setupAsymmetricGridView();
        setupPullToRefresh();
        setupImagePreload();

        return view;
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
        if (!mSearchView.isIconified()) {
            mSearchView.setIconified(true);
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
        mFodexItems = itemsForAdapters();
        if (mFodexItems == null) throw new AssertionError("expect itemsForAdapters to be not null.");

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
        ((AsymmetricGridViewAdapter)mGridView.getAdapter()).setItems(layoutItems);
    }

    private void setupFloatingButtonsAndMenu() {
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

        mTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedItems.size() > 0) {
                    showAddTagDialog();
                } else {
                    NoPhotoToast.make(getActivity()).show();
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
    }

    private void setupPullToRefresh() {
        int[] colors = getResources().getIntArray(R.array.google_colors);
        mSwipeRefreshLayout.setColorSchemeColors(colors);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void setupImagePreload() {
        mPreloadRequest = Glide.with(this)
                .fromMediaStore()
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
                    mSuggestionCursor = FodexCore.getMatchedTags(getActivity(), lastTag);
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
    }

    private void showAddTagDialog() {
        InsertTagDialog dialog = InsertTagDialog.newInstance(mSelectedItems.size());
        dialog.setOnClickListener(new InsertTagDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, String[] tags, int which) {
                switch (which) {
                    // click add
                    case DialogInterface.BUTTON_POSITIVE:
                        if (tags != null) {
                            // add tags to DB
                            long[] imageIds = new long[mSelectedItems.size()];
                            for (int i = 0; i < mSelectedItems.size(); i++) {
                                imageIds[i] = mSelectedItems.get(i).fodexItem.id;
                            }
                            FodexCore.addTagsToPhotos(getActivity(), imageIds, tags);

                            // cleanup and show message
                            dialog.dismiss();
                            InsertTagToast.make(getActivity(), mSelectedItems.size()).show();
                            mSelectedItems.clear();
                            if (mFloatingActionMenu.isExpanded()) mFloatingActionMenu.toggle();
                        } else {
                            PleaseInertTagToast.make(getActivity()).show();
                        }
                        break;
                    // click cancel
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        });
        dialog.show(getActivity().getSupportFragmentManager(), "insert_tag");
    }

    private AdapterView.OnItemLongClickListener imageOnLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final FodexItem item =
                    ((FodexLayoutSpecItem) mGridView.getItemAtPosition(position)).fodexItem;
            List<String> tags = FodexCore.getTags(getActivity(), item.id);
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
                        FodexCore.deleteTagFromPhoto(getActivity(), item.id, tag.toString());
                        if (FodexCore.getTags(getActivity(), item.id).size() == 0) {
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show(getActivity().getSupportFragmentManager(), "show_tag");
            } else {
                NoTagToast.make(getActivity()).show();
            }
            return true;
        }
    };

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
                    .load(item.fodexItem.uri)
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
            return mPreloadRequest.load(item.fodexItem.uri);
        }
    }

}
