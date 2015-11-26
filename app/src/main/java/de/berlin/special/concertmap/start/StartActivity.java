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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}