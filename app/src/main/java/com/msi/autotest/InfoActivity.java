package com.msi.autotest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.msi.android.SysMgr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InfoActivity extends AppCompatActivity {

    private static final String TAG = InfoActivity.class.getSimpleName();
    private static final int INFO_CODE_FROM = 1;
    private Button stop_btn;
    private TextView mTextResult;

    private Intent intent;
    private Bundle bundle;
    private int result= -1;
    private String mTestInfo = "";
    private InfoActivity mThis;
    private boolean Debug = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //避免螢幕自動關閉

        if(Debug){
            Log.d(TAG,"onCreate" + Debug);
        }
        //設置標題欄
        String title = "Auto_Test_" + getCurrentVersion("com.msi.autotest");
        this.setTitle(title);

        intent = getIntent();
        bundle = new Bundle();
        return_main(result);

        mTextResult = findViewById(R.id.TestResult);
        stop_btn = findViewById(R.id.stop_btn);
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "stop Button Click-----------");
                finish();
            }
        });
    }

    private String getCurrentVersion(String packageName) {
        try {
            PackageManager packageManager = getPackageManager();
            try {
                PackageInfo info = packageManager.getPackageInfo(packageName, 0);
                if(Debug) {
                    Log.d(TAG, "Version:" + info.versionName);
                }
                return info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                return  "unknown !";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return  "unknown !";
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Message msg = mHandler.obtainMessage(0);
        mHandler.removeMessages(0);
        mHandler.sendMessageDelayed(msg, 1000);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed----------");
    }

    private void return_main(int result){
        bundle.putInt("resultSystemInfo",result);
        intent.putExtras(bundle);
        setResult(0,intent);
        Log.d(TAG,"return_main: resultSystemInfo" + intent + "........."  + "\t" + result);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    String sdk_version = Build.VERSION.RELEASE;
                    mTextResult.append("Android version: "+ sdk_version);
                    if(sdk_version.equals("9")){
                        mTextResult.append("------Pass.\n");
                    }else{
                        mTextResult.append("------Fail.\n");
                    }
                default:
                    break;
            }
        }
    };
    
}