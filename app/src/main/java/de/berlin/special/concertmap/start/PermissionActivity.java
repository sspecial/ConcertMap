package de.berlin.special.concertmap.start;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.berlin.special.concertmap.R;

public class PermissionActivity extends AppCompatActivity {

    private Button settingBtn;
    private Button exitBtn;
    private TextView grantExplanation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        grantExplanation = (TextView) findViewById(R.id.explanationView);
        String explanation = String.format("Change permission in your device's app setting.%n%n" +
                "Allow Concert Map to access the storage in your device in order to store images of events and artists.");
        grantExplanation.setText(explanation);

        settingBtn = (Button) findViewById(R.id.buttonSetting);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();

            }
        });

        exitBtn = (Button) findViewById(R.id.buttonExit);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
