package de.berlin.special.concertmap.navigate;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;


public class NavigationActivity extends AppCompatActivity {

    private static final int NAV_CASE_CITY = 0;
    private static final int NAV_CASE_TRACKED_ARTISTS  = 1;
    private static final int NAV_CASE_ATTENDED_EVENTS  = 2;

    // Navigation bar items
    private String navItem1;
    private final String navItem2 = "Tracked Artists";
    private final String navItem3 = "Attended Events";

    private String[] eventNavItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar actionBar;
    private CharSequence mTitle;
    private NavigateAdapter myAdapter;

    FragmentManager manager;

    Fragment eventListFragment;
    Bundle args = new Bundle();

    Fragment artistListFragment = new ArtistListFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Constructing array used ba navigation bar
        navItem1 = Utility.city + " Concerts";
        eventNavItems = new String[]{navItem1, navItem2, navItem3};

        // Initiating GeoFragment as default view of activity
        manager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            eventListFragment = new EventListFragment();
            args.clear();
            args.putString(Utility.FRAG_EL_TYPE, Utility.FRAG_EL_GEO);
            eventListFragment.setArguments(args);
            manager.beginTransaction().add(R.id.content_frame, eventListFragment).commit();
        }

        actionBar = getSupportActionBar();
        myAdapter = new NavigateAdapter(this, eventNavItems);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        // Setting ItemClickListener for DrawerList
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        // Setting Adaptor for DrawerList
        mDrawerList.setAdapter(myAdapter);
        // Setting Listener for DrawerList
        setUpDrawerToggle();
    }

    /** Handling navigation click events */
    private void selectItem(int position) {

        if (mDrawerList != null) {
            mDrawerList.setItemChecked(position, true);
            setTitle(eventNavItems[position]);
            initFrag(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    public void initFrag(int position) {
        manager.executePendingTransactions();
        switch (position) {

            case NAV_CASE_CITY:
                eventListFragment = new EventListFragment();
                args.clear();
                args.putString(Utility.FRAG_EL_TYPE, Utility.FRAG_EL_GEO);
                eventListFragment.setArguments(args);
                manager.beginTransaction()
                        .replace(R.id.content_frame, eventListFragment)
                        .commit();
                break;

            case NAV_CASE_TRACKED_ARTISTS:
                manager.beginTransaction()
                        .replace(R.id.content_frame, artistListFragment)
                        .commit();
                break;

            case NAV_CASE_ATTENDED_EVENTS:
                eventListFragment = new EventListFragment();
                args.clear();
                args.putString(Utility.FRAG_EL_TYPE, Utility.FRAG_EL_ATTENDED);
                eventListFragment.setArguments(args);
                manager.beginTransaction()
                        .replace(R.id.content_frame, eventListFragment)
                        .commit();
                break;

            default:
                break;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        actionBar.setTitle(mTitle);
    }

    public void setUpDrawerToggle() {

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                      /* host Activity */
                mDrawerLayout,             /* DrawerLayout object */
                R.drawable.ic_drawer,      /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                actionBar.setTitle(mTitle);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                actionBar.setTitle(mTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // set up the drawer's list view with items and click listener
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menu2);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

class NavigateAdapter extends BaseAdapter {

    private Context context;
    private String[] eventNavItems;
    private int[] images = {R.drawable.cornet_ins, R.drawable.music_conductor, R.drawable.audio_wave};

    public NavigateAdapter(Context context, String[] eventNavItems) {
        this.context = context;
        this.eventNavItems = eventNavItems;
    }

    @Override
    public int getCount() {
        return eventNavItems.length;
    }

    @Override
    public Object getItem(int i) {
        return eventNavItems[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View row = null;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_row, viewGroup, false);
        }
        else {
            row = view;
        }
        TextView titleTextView = (TextView) row.findViewById(R.id.row_textView);
        ImageView titleImageView = (ImageView) row.findViewById(R.id.row_imageView);

        titleTextView.setText(eventNavItems[i]);
        titleImageView.setImageResource(images[i]);

        return row;
    }
}
