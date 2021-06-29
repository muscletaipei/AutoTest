package com.msi.autotest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

public class EmmcActivity extends AppCompatActivity {

    private static final String TAG = EmmcActivity.class.getSimpleName();
    private boolean Debug = false;
    private TextView mTextResult;
    private TextView mTextTitle;
    private TextView tvShowCounts;

    private Button stop_btn;
    private String mTestEMMC = "";
    private int result = -1;

    private String SDCardPath = "";
    private String eMMCPath = "";
    private int totalTimes = 1;
    private int testCounts = 1;
    private File parameterFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emmc);

        if(Debug){
            Log.d(TAG,"onCreate" + Debug);
        }
        //設置標題欄
        String title = "Auto_Test_" + getCurrentVersion("com.msi.autotest");
        this.setTitle(title);


        mTextResult = findViewById(R.id.TestResult);
        mTextTitle = (TextView) findViewById(R.id.TestResultTitle);
        tvShowCounts = (TextView) findViewById(R.id.showcounts);

        stop_btn = findViewById(R.id.stop_btn);
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "stop Button Click-----------");
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

        tvShowCounts.setText("Test counts:");
        mTextResult.setText("");
        SDCardPath = Storage.getExternalMounts("SDCard")+"/";
        if(Debug){
            Log.d(TAG,"SDCardPath:"+SDCardPath);
        }
        if(SDCardPath.equals("/")){
            mTextResult.setText("Please insert SD card.");
        }else{
            parameterFile = new File(SDCardPath+"Parameter.txt");
            if(!parameterFile.exists()){
                mTextResult.setText("Please input Parameter.txt file.");
            }else{
                totalTimes = Integer.parseInt(Storage.readParameter("Test times"));
                testCounts = 1;
                Message msg = mHandler.obtainMessage(4);
                mHandler.removeMessages(4);
                mHandler.sendMessageDelayed(msg, 1000);
             }
        }

        Message msg = mHandler.obtainMessage(0);
        mHandler.removeMessages(0);
        mHandler.sendMessageDelayed(msg, 2000);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed----------");
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    boolean is_error = false;

                    Log.d(TAG, "is_error----------" + is_error);

                    if (!is_error) {
                        result = 1;
                        callpass();
                    } else {
                        result = 0;
                        callfail();
                    }
                    break;
                case 1:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    private void callpass() {
//        writeLog(mTestInfo, mTextResult.getText().toString(), mTestInfo+".log");
        mTextResult.append("\n\nTest "+ mTestEMMC +" Pass!");
        mTextTitle.setText(mTestEMMC +" Pass!");
        Intent intent2 = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(mTestEMMC + "eMMCResult", result);
        Log.d(TAG, "callpass: " + result);

        intent2.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent2);
        finish();

    }
    private void callfail() {
        mTextResult.append("\n\nTest "+ mTestEMMC +" Fail!");
        mTextTitle.setText(mTestEMMC +" Fail!");
//        writeLog(mTestInfo,mTextResult.getText().toString(),mTestInfo+"_fail.log");

        Intent intent2 = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(mTestEMMC + "eMMCResult", result);
        Log.d(TAG, "callfail: " + result);

        intent2.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent2);
        Message msg = mHandler.obtainMessage(1);
        mHandler.removeMessages(1);
        mHandler.sendMessageDelayed(msg, 2*1000);

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
}