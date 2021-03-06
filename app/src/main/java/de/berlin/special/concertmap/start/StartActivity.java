package de.berlin.special.concertmap.start;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.service.FetchIntentService;
import de.berlin.special.concertmap.util.ImageDirectories;
import de.berlin.special.concertmap.util.Utility;

public class StartActivity extends AppCompatActivity {

    Fragment initiateFragment = new InitiateFragment();
    Fragment startFragment = new StartFragment();
    private static final String LOG_TAG = StartActivity.class.getSimpleName();
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
        ImageDirectories.organize();
    }

    @Override
    public void onResume() {
        super.onResume();

        Context mContext = getApplicationContext();
        Intent alarmIntent = new Intent(mContext, FetchIntentService.AlarmReceiver.class);

        //Wrap in a pending intent which only fires once.
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);//getBroadcast(context, 0, i, 0);

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Calendar firingCal = Calendar.getInstance();
        firingCal.set(Calendar.HOUR_OF_DAY, 24); // At the hour you want to fire the alarm
        firingCal.set(Calendar.MINUTE, 0); // alarm minute
        firingCal.set(Calendar.SECOND, 30); // and alarm second
        Log.d(LOG_TAG, "****-----------------" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(firingCal.getTime()));
        long intendedTime = firingCal.getTimeInMillis();

        //Set the AlarmManager to wake up the system.
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, intendedTime, AlarmManager.INTERVAL_DAY , pi);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}