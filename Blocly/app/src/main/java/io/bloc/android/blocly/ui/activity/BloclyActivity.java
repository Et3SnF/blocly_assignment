package io.bloc.android.blocly.ui.activity;

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

import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.model.RssFeed;
import io.bloc.android.blocly.ui.adapter.ItemAdapter;
import io.bloc.android.blocly.ui.adapter.NavigationDrawerAdapter;

// ActionBarActivity is required to use when I have Theme.AppCompat in the styles.xml
// This has backwards compatible features

public class BloclyActivity extends ActionBarActivity implements NavigationDrawerAdapter.NavigationDrawerAdapterDelegate {

    private ItemAdapter itemAdapter;

    // Variables for drawer layout

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    // Variables for drawer ViewGroup

    private NavigationDrawerAdapter navigationDrawerAdapter;

    // Variables for options menu and overflow button

    private Menu menu;
    private View overflowButton;

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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_activity_blocly);
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

    }

    // When Options menu is created, inflate the blocly menu layout

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.blocly, menu);
        this.menu = menu;
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

}
