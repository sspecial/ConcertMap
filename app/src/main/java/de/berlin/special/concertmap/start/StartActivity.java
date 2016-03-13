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
import java.util.Calendar;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.service.FetchIntentService;
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

        Context mContext = getApplicationContext();
        Intent alarmIntent = new Intent(mContext, FetchIntentService.AlarmReceiver.class);

        //Wrap in a pending intent which only fires once.
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);//getBroadcast(context, 0, i, 0);

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Calendar firingCal = Calendar.getInstance();
        firingCal.set(Calendar.HOUR, 0); // At the hour you want to fire the alarm
        firingCal.set(Calendar.MINUTE, 5); // alarm minute
        firingCal.set(Calendar.SECOND, 0); // and alarm second
        long intendedTime = firingCal.getTimeInMillis();

        //Set the AlarmManager to wake up the system.
        am.setRepeating(AlarmManager.RTC_WAKEUP, intendedTime, AlarmManager.INTERVAL_DAY , pi);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}