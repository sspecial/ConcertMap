package de.berlin.special.concertmap.start;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.net.URL;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.service.FetchIntentService;
import de.berlin.special.concertmap.util.BuildURL;
import de.berlin.special.concertmap.util.Utility;

public class StartActivity extends AppCompatActivity {

    Fragment initiateFragment = new InitiateFragment();
    Fragment startFragment = new StartFragment();
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Getting setting from shared preferences
        settings = this.getSharedPreferences(Utility.PREFS_NAME, Context.MODE_PRIVATE);
        String city = settings.getString(Utility.SETTING_CITY, Utility.CITY_IS_UNKNOWN);

        if (city.equals(Utility.CITY_IS_UNKNOWN)) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, initiateFragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, startFragment)
                    .commit();
        }
        getSupportActionBar().hide();
    }

    @Override
    public void onStart() {
        super.onStart();
        /*
        File dir = new File(Utility.imageDirPath());
        if (dir.exists()) {
            for (File imFile : dir.listFiles()) {
                imFile.delete();
            }
            dir.delete();
        }
        */
        File artistDir = new File(Utility.IMAGE_DIR_ARTIST);
        if (!artistDir.exists()) {
            artistDir.mkdirs();
        }

        File eventDir = new File(Utility.IMAGE_DIR_EVENT);
        if (!eventDir.exists()) {
            eventDir.mkdirs();
        }

        File dailyDir = new File(Utility.IMAGE_DIR_DAILY);

        if (!dailyDir.exists()) {
            dailyDir.mkdirs();
        } else {
            for (File imFile : dailyDir.listFiles()) {
                if(!Utility.imageDirToday().equals(imFile.getAbsolutePath())) {
                    if(imFile.isDirectory()) {
                        for (File image : imFile.listFiles()) {
                            image.delete();
                        }
                        imFile.delete();
                    }else {
                        imFile.delete();
                    }
                }
            }
        }

        File todayDir = new File(Utility.imageDirToday());
        if (!todayDir.exists()) {
            todayDir.mkdirs();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        BuildURL.instance().init(getApplicationContext());
        URL url = BuildURL.instance().buildGeoEventsURL();
        Context mContext = getApplicationContext();

        Intent alarmIntent = new Intent(mContext, FetchIntentService.AlarmReceiver.class);
        alarmIntent.putExtra(Utility.URL, url.toString());

        //Wrap in a pending intent which only fires once.
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);//getBroadcast(context, 0, i, 0);

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        //Set the AlarmManager to wake up the system.
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}