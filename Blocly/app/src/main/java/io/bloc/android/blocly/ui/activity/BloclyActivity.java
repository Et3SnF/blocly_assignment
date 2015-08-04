package io.bloc.android.blocly.ui.activity;

import android.animation.ValueAnimator;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.DataSource;
import io.bloc.android.blocly.api.model.RssFeed;
import io.bloc.android.blocly.api.model.RssItem;
import io.bloc.android.blocly.ui.adapter.NavigationDrawerAdapter;
import io.bloc.android.blocly.ui.fragment.RssItemListFragment;

// ActionBarActivity is required to use when I have Theme.AppCompat in the styles.xml
// This has backwards compatible features

public class BloclyActivity extends ActionBarActivity implements
        NavigationDrawerAdapter.NavigationDrawerAdapterDelegate,
        NavigationDrawerAdapter.NavigationDrawerAdapterDataSource,
        RssItemListFragment.Delegate {

    // Variables for drawer layout

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    // Variables for drawer ViewGroup

    private NavigationDrawerAdapter navigationDrawerAdapter;

    // Variables for options menu and overflow button

    private Menu menu;
    private View overflowButton;

    private List<RssFeed> allFeeds = new ArrayList<RssFeed>();
    private RssItem expandedItem = null;

    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocly);

        // Add the toolbar support here

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_activity_blocly);
        setSupportActionBar(toolbar);

        // Integrate drawer layout below. We have to do this here b/c it doesn't apply to just
        // that viewholder in ItemAdapter.java. Whole different story

        // Instance of ActionBar to give space to make way of menu icon instead of the app's icon

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.dl_activity_blocly);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0) {

            // Created an anonymous class to add actions when drawer is opened and closed
            // (to disable options menu)

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                // Re-enable the button

                if(overflowButton != null) {

                    overflowButton.setAlpha(1f);
                    overflowButton.setEnabled(true);

                }

                if(menu == null) {
                    return;
                }

                for(int i = 0; i < menu.size(); i++) {

                    MenuItem item = menu.getItem(i);

                    // For drawer not to interfere with the share icon behavior
                    // After closing the drawer, do not enable share action if no items are expanded!

                    if(item.getItemId() == R.id.action_share && expandedItem == null) {
                        continue;
                    }

                    item.setEnabled(true);

                    Drawable icon = item.getIcon();

                    if(icon != null) {
                        icon.setAlpha(255);
                    }
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                // Disable the overflow/settings button when drawer is opened

                if(overflowButton != null) {
                    overflowButton.setEnabled(false);
                }

                if(menu == null) {
                    return; //return nothing
                }

                // Disable all of the menu buttons when this drawer is opened

                for(int i = 0; i < menu.size(); i++) {
                    menu.getItem(i).setEnabled(false);
                }

            }

            // The opacity of the menu items will be changed as the drawer is slid out

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                super.onDrawerSlide(drawerView, slideOffset);

                if(overflowButton == null) {

                    ArrayList<View> foundViews = new ArrayList<View>();
                    getWindow().getDecorView().findViewsWithText(foundViews,
                            getString(R.string.abc_action_menu_overflow_description),
                            View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);

                    if(foundViews.size() > 0) {
                        overflowButton = foundViews.get(0);
                    }

                }

                if(overflowButton != null) {
                    overflowButton.setAlpha(1f - slideOffset);
                }

                if(menu == null) {
                    return;
                }

                for(int i = 0; i < menu.size(); i++) {

                    MenuItem item = menu.getItem(i);

                    // Do not change the opacity of the share icon if it wasn't visible in the first
                    // place!

                    if(item.getItemId() == R.id.action_share && expandedItem == null) {
                        continue;
                    }

                    Drawable icon = item.getIcon();

                    if(icon != null) {
                        icon.setAlpha((int)((1f-slideOffset)*255));
                    }

                }


            }
        };

        // Put in that "menu switch" and place a listener in it

        drawerLayout.setDrawerListener(drawerToggle);

        // Inflate the RecyclerView for the drawer
            // Remember: The RecyclerView needs 4 things: adapter, LayoutManager, animator, and its layout

        navigationDrawerAdapter = new NavigationDrawerAdapter();
        RecyclerView navigationRecyclerView = (RecyclerView) findViewById(R.id.rv_nav_activity_blocly);
        navigationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        navigationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        navigationRecyclerView.setAdapter(navigationDrawerAdapter);

        BloclyApplication.getSharedDataSource().fetchAllFeeds(new DataSource.Callback<List<RssFeed>>() {

            @Override
            public void onSuccess(List<RssFeed> rssFeeds) {

                allFeeds.addAll(rssFeeds);
                navigationDrawerAdapter.notifyDataSetChanged();

                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.fl_activity_blocly, RssItemListFragment.fragmentForRssFeed(rssFeeds.get(0)),
                                "main_fragment")
                        .commit();
            }

            @Override
            public void onError(String errorMessage) {

            }
        });

        // Set the delegate class

        navigationDrawerAdapter.setDelegate(this);
        navigationDrawerAdapter.setDataSource(this);

        manager = getFragmentManager();
    }

    @Override
    public void onBackPressed() {

        if(manager.getBackStackEntryCount() > 0) {
            manager.popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }

    // When Options menu is created, inflate the blocly menu layout

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.blocly, menu);
        this.menu = menu;
        animateShareItem(expandedItem != null); //animate share icon when expanded
        return super.onCreateOptionsMenu(menu);

    }

    // These methods are required when something in the Activity changes. The drawer needs to
    // change with it. Methods are from activity class

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Creating the intent of when the user presses the share button

        if(item.getItemId() == R.id.action_share) {

            RssItem itemToShare = expandedItem;

            if(itemToShare == null) {
                return false;
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, String.format("%s (%s)", itemToShare.getTitle(), itemToShare.getUrl()));
            shareIntent.setType("text/plain");
            Intent chooser = Intent.createChooser(shareIntent, getString(R.string.share_chooser_title));
            startActivity(chooser);

        }
        else {
            Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);

    }

    /**
     *
     * NavigationDrawerAdapterDelegate
     *
     */

    @Override
    public void didSelectNavigationOption(NavigationDrawerAdapter adapter,
                                          NavigationDrawerAdapter.NavigationOption navigationOption) {
        drawerLayout.closeDrawers();
        Toast.makeText(this, "Show the " + navigationOption.name(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void didSelectFeed(final NavigationDrawerAdapter navigationDrawerAdapter, final RssFeed rssFeed) {
        drawerLayout.closeDrawers();

        if(rssFeed.equals(rssFeed)) {
            Log.v("Fragment Log", "You have selected the same feed");
            return;
        }
        else if(getFragmentManager().getBackStackEntryCount() > 0 && !rssFeed.equals(rssFeed)) {

            final RssItemListFragment fragment_switch = (RssItemListFragment) getFragmentManager()
                    .findFragmentByTag(String.valueOf(rssFeed.getRowId()));

            BloclyApplication.getSharedDataSource().fetchAllFeeds(new DataSource.Callback<List<RssFeed>>() {

                @Override
                public void onSuccess(List<RssFeed> rssFeeds) {

                    allFeeds.addAll(rssFeeds);
                    navigationDrawerAdapter.notifyDataSetChanged();

                    manager.beginTransaction()
                            .replace(R.id.fl_activity_blocly, fragment_switch)
                            .addToBackStack(String.valueOf(rssFeed.getRowId())).commit();
                }

                @Override
                public void onError(String errorMessage) {
                    Log.v("Fragment Log", "Could not find fragment.");
                }
            });

        }
        else {

            final RssItemListFragment fragment = RssItemListFragment.fragmentForRssFeed(rssFeed);

            BloclyApplication.getSharedDataSource().fetchAllFeeds(new DataSource.Callback<List<RssFeed>>() {

                @Override
                public void onSuccess(List<RssFeed> rssFeeds) {

                    allFeeds.addAll(rssFeeds);
                    navigationDrawerAdapter.notifyDataSetChanged();

                    manager.beginTransaction()
                            .replace(R.id.fl_activity_blocly, fragment)
                            .addToBackStack(String.valueOf(rssFeed.getRowId())).commit();
                }

                @Override
                public void onError(String errorMessage) {
                    Log.v("Fragment Log", "Could not find fragment.");
                }
            });

        }

    }

    /**
     *
     * RssListFragment.Delegate
     *
     */

    @Override
    public void onItemExpanded(RssItemListFragment rssItemListFragment, RssItem rssItem) {
        expandedItem = rssItem;
        animateShareItem(expandedItem != null);
    }

    public void onItemContracted(RssItemListFragment rssItemListFragment, RssItem rssItem) {

        if (expandedItem == rssItem) {
            expandedItem = null;
        }

        animateShareItem(expandedItem != null);
    }

    @Override
    public void onItemVisitClicked(RssItemListFragment rssItemListFragment, RssItem rssItem) {

        Intent visitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(rssItem.getUrl()));
        startActivity(visitIntent);

    }

    /**
     *
     * NavigationDrawerAdapterDataSource
     *
     */

    @Override
    public List<RssFeed> getFeeds(NavigationDrawerAdapter adapter) {
        return allFeeds;
    }

    // ------------ Separate Methods Area --------------- //

    /*
     * Method to animate share icon when item is expanded
     *
     */

    private void animateShareItem(final boolean enabled) {

        MenuItem shareItem = menu.findItem(R.id.action_share);

        if(shareItem.isEnabled() == enabled) {
            return; // Stop the method
        }

        shareItem.setEnabled(enabled);

        final Drawable shareIcon = shareItem.getIcon();

        ValueAnimator valueAnimator = ValueAnimator.ofInt(enabled ? new int[] {0, 255} : new int[] {255,0});
        valueAnimator.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                shareIcon.setAlpha((Integer) animation.getAnimatedValue());
            }

        });

        valueAnimator.start();

    }

}
