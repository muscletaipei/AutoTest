package com.msi.autotest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RtcActivity extends AppCompatActivity {

    private static final String TAG = RtcActivity.class.getSimpleName();
    private Button stop_btn;
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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