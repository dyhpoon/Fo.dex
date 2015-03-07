package com.dyhpoon.fodex;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;
import com.dyhpoon.fodex.data.FodexCore;
import com.dyhpoon.fodex.data.SearchViewCursorAdapter;
import com.dyhpoon.fodex.navigationDrawer.NavigationDrawerCallbacks;
import com.dyhpoon.fodex.navigationDrawer.NavigationDrawerData;
import com.dyhpoon.fodex.navigationDrawer.NavigationDrawerFragment;
import com.dyhpoon.fodex.navigationDrawer.NavigationDrawerInfo;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        mTitle = getTitle();

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);

            // setup searchview
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    Cursor cursor = FodexCore.getMatchedTags(MainActivity.this, s);
                    SearchViewCursorAdapter adapter = new SearchViewCursorAdapter(MainActivity.this, cursor, true);
                    searchView.setSuggestionsAdapter(adapter);
                    return true;
                }
            });

            // set hints
            EditText searchText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchText.setHint(R.string.hashtags);

            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerPageItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        try {
            NavigationDrawerInfo info = NavigationDrawerData.getPageItem(this, position);
            fragment = (Fragment) info.classType.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNavigationDrawerUtilityItemSelected(int position) {
        // TODO: start Setting Activity.
    }

    public void onSectionAttached(int number) {
        mTitle = NavigationDrawerData.getPageItem(this, number - 1).title;
    }

}
