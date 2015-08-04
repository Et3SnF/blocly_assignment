package io.bloc.android.blocly.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.model.RssFeed;
import io.bloc.android.blocly.api.model.RssItem;
import io.bloc.android.blocly.ui.adapter.ItemAdapter;
import io.bloc.android.blocly.ui.adapter.NavigationDrawerAdapter;

// ActionBarActivity is required to use when I have Theme.AppCompat in the styles.xml
// This has backwards compatible features

public class BloclyActivity extends ActionBarActivity implements
        NavigationDrawerAdapter.NavigationDrawerAdapterDelegate, ItemAdapter.DataSource,
        ItemAdapter.Delegate {

    private ItemAdapter itemAdapter;

    // Variables for drawer layout

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    // Variables for drawer ViewGroup

    private NavigationDrawerAdapter navigationDrawerAdapter;

    // Variables for options menu and overflow button

    private Menu menu;
    private View overflowButton;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocly);

        // Add the toolbar support here

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_activity_blocly);
        setSupportActionBar(toolbar);

        // Adapter for RecyclerView

        itemAdapter = new ItemAdapter();

        // Display the recyclerView, which contains the adapter, layout manager, and animator

        recyclerView = (RecyclerView) findViewById(R.id.rv_activity_blocly);
        // #12
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemAdapter);

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

                // Set the share alpha to 0 and disable it for now

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

        // Set the delegate class

        navigationDrawerAdapter.setDelegate(this);

        // Set the delegate

        itemAdapter.setDataSource(this);
        itemAdapter.setDelegate(this);

    }

    // When Options menu is created, inflate the blocly menu layout

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.blocly, menu);
        this.menu = menu;

        // Set the visibility and functionality of the menu button to false
        // Make it show whenever a feed item is clicked

        menu.getItem(0).setEnabled(false);
        menu.getItem(0).setVisible(false);

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

        // Create a toast whenever a menu item is pressed

        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();

        if(item.getTitle().equals("Share")) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Take a look at this feed!");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

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
    public void didSelectFeed(NavigationDrawerAdapter navigationDrawerAdapter, RssFeed rssFeed) {
        drawerLayout.closeDrawers();
        Toast.makeText(this, "Show RSS items from " + rssFeed.getTitle(), Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * DataSource method implementation
     *
     */

    @Override
    public int getItemCount(ItemAdapter itemAdapter) {
        return BloclyApplication.getSharedDataSource().getItems().size();
    }

    @Override
    public RssItem getRssItem(ItemAdapter itemAdapter, int position) {
        return BloclyApplication.getSharedDataSource().getItems().get(position);
    }

    @Override
    public RssFeed getRssFeed(ItemAdapter itemAdapter, int position) {
        return BloclyApplication.getSharedDataSource().getFeeds().get(0);
    }

    /**
     *
     * Delegate method implementation
     *
     */

    @Override
    public void onItemClicked(ItemAdapter itemAdapter, RssItem rssItem) {

        int positionToExpand = -1;
        int positionToContract = -1;

        // If view is already opened, contract it

        if (itemAdapter.getExpandedItem() != null) {
            positionToContract = BloclyApplication.getSharedDataSource().getItems().indexOf(itemAdapter.getExpandedItem());

            View viewToContract = recyclerView.getLayoutManager().findViewByPosition(positionToContract);

            if(viewToContract == null) {
                positionToContract = -1;
            }

        }

        // If view is selected, recover the position. Then set the expanded item.
        // When the user clicks on the expanded item, contract it. (null)

        if (itemAdapter.getExpandedItem() != rssItem) {
            positionToExpand = BloclyApplication.getSharedDataSource().getItems().indexOf(rssItem);
            itemAdapter.setExpandedItem(rssItem);
            menu.getItem(0).setEnabled(true);
            menu.getItem(0).setVisible(true);
        }
        else {
            itemAdapter.setExpandedItem(null);
            menu.getItem(0).setEnabled(false);
            menu.getItem(0).setVisible(false);
        }

        // Notify for changes --> goes to ItemAdapter's update(RssFeed, RssItem) method

        if (positionToContract > -1) {
            itemAdapter.notifyItemChanged(positionToContract);
        }

        if (positionToExpand > -1) {
            itemAdapter.notifyItemChanged(positionToExpand);
        }
        // When there is no view to expand, avoid scrolling the RecyclerView
        else {
            return;
        }

        int lessToScroll = 0;

        if(positionToContract > -1 && positionToContract < positionToExpand) {
            lessToScroll = itemAdapter.getExpandedItemHeight() - itemAdapter.getCollapsedItemHeight();
        }

        // getTop() is the distance between the top of the View and top if its parent. We have a flaw
        // because the when we press a new view, the distance of the new view to the top is measured.
        // Then the old view is contracted and then scrolled to the new view. We are scrolling an
        // additional distance between contracted and expanded of one view...this needs to be taken
        // into account

        // Took into account of the scroll factor with lessToScroll variable

        View viewToExpand = recyclerView.getLayoutManager().findViewByPosition(positionToExpand);
        recyclerView.smoothScrollBy(0, viewToExpand.getTop() - lessToScroll);

    }
}
