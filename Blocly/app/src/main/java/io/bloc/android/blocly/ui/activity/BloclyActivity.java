package io.bloc.android.blocly.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import io.bloc.android.blocly.R;
import io.bloc.android.blocly.ui.adapter.ItemAdapter;

// ActionBarActivity is required to use when I have Theme.AppCompat in the styles.xml
// This has backwards compatible features

public class BloclyActivity extends ActionBarActivity {

    private ItemAdapter itemAdapter;

    // Variables for drawer layout

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocly);

        // Add the toolbar support here. Set toolbar as an action bar

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
        // The boolean setDisplayHomeAsUpEnabled toggles whether we should display the menu icon or
        // not

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.dl_activity_blocly);

        // Put in that "menu switch" and place a listener in it

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        drawerLayout.setDrawerListener(drawerToggle);

        // Default DrawerLayout to open at launch

        drawerLayout.openDrawer(Gravity.LEFT);

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

        return super.onOptionsItemSelected(item);

    }
}
