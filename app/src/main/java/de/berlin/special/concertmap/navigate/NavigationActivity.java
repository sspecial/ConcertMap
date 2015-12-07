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
import de.berlin.special.concertmap.util.Utility;


public class NavigationActivity extends AppCompatActivity {

    private static final int NAV_CASE_CITY = 0;
    private static final int NAV_CASE_TRACKED_ARTISTS  = 1;
    private static final int NAV_CASE_ATTENDED_EVENTS  = 2;
    private static final int NAV_CASE_SEARCH_AN_ARTIST  = 3;
    private static final int NAV_CASE_SETTING = 4;
    private static final int NAV_CASE_EXIT = 5;

    // Navigation bar items
    private String navItem1;
    private final String navItem2 = "Tracked Artists";
    private final String navItem3 = "Attended Events";
    private final String navItem4 = "Search an Artist";
    private final String navItem5 = "Settings";
    private final String navItem6 = "Exit";

    private String[] eventNavItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ActionBar actionBar;
    private CharSequence mTitle;
    private NavigateAdapter myAdapter;

    FragmentManager manager;
    Fragment geoListFragment = new GeoListFragment();
    Fragment eventListFragment = new EventListFragment();
    Fragment artistListFragment = new ArtistListFragment();
    Fragment searchArtistFragment = new SearchArtistFragment();
    Fragment settingFragment = new SettingFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Constructing array used ba navigation bar
        if (!Utility.city.equals(Utility.CITY_IS_UNKNOWN))
            navItem1 = Utility.city + " Concerts";
        else
            navItem1 = "Concerts";
        eventNavItems = new String[]{navItem1, navItem2, navItem3, navItem4, navItem5, navItem6};

        // Initiating GeoFragment as default view of activity
        manager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            manager.beginTransaction()
                    .add(R.id.content_frame, geoListFragment)
                    .commit();
        }

        actionBar = getSupportActionBar();
        myAdapter = new NavigateAdapter(this, eventNavItems);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        View header = getLayoutInflater().inflate(R.layout.custom_navigate_header, null);
        // Setting ItemClickListener for DrawerList
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                selectItem(position-1);
            }
        });
        // Adding Header to DrawerList
        mDrawerList.addHeaderView(header);
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
                manager.beginTransaction()
                        .replace(R.id.content_frame, geoListFragment)
                        .commit();
                break;

            case NAV_CASE_TRACKED_ARTISTS:
                manager.beginTransaction()
                        .replace(R.id.content_frame, artistListFragment)
                        .commit();
                break;

            case NAV_CASE_ATTENDED_EVENTS:
                manager.beginTransaction()
                        .replace(R.id.content_frame, eventListFragment)
                        .commit();
                break;

            case NAV_CASE_SEARCH_AN_ARTIST:
                manager.beginTransaction()
                        .replace(R.id.content_frame, searchArtistFragment)
                        .commit();
                break;

            case NAV_CASE_SETTING:
                manager.beginTransaction()
                        .replace(R.id.content_frame, settingFragment)
                        .commit();
                break;

            case NAV_CASE_EXIT:
                System.exit(0);
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

    @Override
    public void onBackPressed() {

        Fragment currentFragment = manager.findFragmentById(R.id.content_frame);
        if (currentFragment.toString().contains(GeoListFragment.class.getSimpleName())) {
            finish();
        } else {
            manager.beginTransaction()
                    .replace(R.id.content_frame, geoListFragment)
                    .addToBackStack(null)
                    .commit();
            this.setTitle("Concert Map");
        }
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
        getMenuInflater().inflate(R.menu.navigation, menu);
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

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_LINE = 0;
    private static final int VIEW_TYPE_EVENT_ROW = 1;

    private Context context;
    private String[] eventNavItems;
    private int[] images = {R.drawable.cornet_ins
            , R.drawable.music_conductor
            , R.drawable.audio_wave
            , R.drawable.search
            , R.drawable.settings
            , R.drawable.exit};

    public NavigateAdapter(Context context, String[] eventNavItems) {
        this.context = context;
        this.eventNavItems = eventNavItems;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 4)
            return VIEW_TYPE_LINE;
        else
            return VIEW_TYPE_EVENT_ROW;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
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

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = null;
        int viewType = getItemViewType(i);
        switch (viewType) {
            case VIEW_TYPE_EVENT_ROW: {
                row = inflater.inflate(R.layout.custom_navigate_row, viewGroup, false);
                break;
            }
            case VIEW_TYPE_LINE: {
                row = inflater.inflate(R.layout.custom_navigate_line, viewGroup, false);
                break;
            }
        }
        TextView titleTextView = (TextView) row.findViewById(R.id.row_textView);
        ImageView titleImageView = (ImageView) row.findViewById(R.id.row_imageView);
        titleTextView.setText(eventNavItems[i]);
        titleImageView.setImageResource(images[i]);
        return row;
    }
}
