package com.msi.autotest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class RtcActivity extends AppCompatActivity {

    private static final String TAG = RtcActivity.class.getSimpleName();
    private Button stop_btn;
    private TextView resultView;
    private String RTC_time_start = "";
    private String RTC_time_end = "";
    private String RTC_default_path="/data/data/com.msi.autotest/hwclock_default.txt";
    private String RTC_start_path="/data/data/com.msi.autotest/hwclock_start.txt";
    private String RTC_end_path="/data/data/com.msi.autotest/hwclock_end.txt";
    private String locale = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();

        if (locale.equalsIgnoreCase("ENGLISH")) {
            config.locale = Locale.US;
            resources.updateConfiguration(config, dm);
        } else if (locale.equalsIgnoreCase("CHINESE")) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
            resources.updateConfiguration(config, dm);
        }

        setContentView(R.layout.activity_rtc);

        stop_btn = findViewById(R.id.stop_btn);
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Log.d(TAG, "stop Button Click-----------" );
            }
        });

        resultView = findViewById(R.id.TestResult);

    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed------------");
        super.onBackPressed();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();


    }

}