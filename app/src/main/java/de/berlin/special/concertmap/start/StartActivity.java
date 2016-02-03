package de.berlin.special.concertmap.start;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import de.berlin.special.concertmap.R;
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
    protected void onDestroy() {
        super.onDestroy();
    }
}