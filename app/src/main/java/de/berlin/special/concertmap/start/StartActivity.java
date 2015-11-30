package de.berlin.special.concertmap.start;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;


public class StartActivity extends AppCompatActivity {

    InitiateFragment initiateFragment = new InitiateFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, initiateFragment)
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