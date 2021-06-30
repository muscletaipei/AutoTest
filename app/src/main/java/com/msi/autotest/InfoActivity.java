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
import java.util.concurrent.BlockingDeque;

public class InfoActivity extends AppCompatActivity {

    private static final String TAG = InfoActivity.class.getSimpleName();
    private static final int INFO_CODE_FROM = 100;
    private Button stop_btn;
    private TextView mTextResult;
    private TextView mTextTitle;

    private int result = -1;

    private String mTestInfo = "";
    private boolean Debug = false;
    private String m_version = "DUO-6.3.3_05-21-2021";
    private String os_version = "9";

    private SpannableString fail_mesg = new SpannableString( " Fail !");

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

        mTextResult = findViewById(R.id.TestResult);
        mTextTitle = (TextView) findViewById(R.id.TestResultTitle);

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
        Message msg = mHandler.obtainMessage(0);
        mHandler.removeMessages(0);
        mHandler.sendMessageDelayed(msg, 2000);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed----------");
    }


    private void callpass() {
//        writeLog(mTestInfo, mTextResult.getText().toString(), mTestInfo+".log");
        mTextResult.append("\n\nTest "+ mTestInfo +" Pass!");
        mTextTitle.setText(mTestInfo +" Pass!");
        Intent intent2 = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(mTestInfo + "Pass", result);
        Log.d(TAG, "callpass: " + result);

        intent2.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent2);
        finish();

    }

    private void callfail() {
        mTextResult.append("\n\nTest "+ mTestInfo +" Fail!");
        mTextTitle.setText(mTestInfo +" Fail!");
//        writeLog(mTestInfo,mTextResult.getText().toString(),mTestInfo+"_fail.log");

        Intent intent2 = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(mTestInfo + "Pass", result);
        Log.d(TAG, "callfail: " + result);

        intent2.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent2);
        Message msg = mHandler.obtainMessage(1);
        mHandler.removeMessages(1);
        mHandler.sendMessageDelayed(msg, 2*1000);

    }
//    private void writeLog(String function, String reason, String file_name) {
//        Log.d(TAG, "writeLog------------");
//        RecordFailReason re = new RecordFailReason(function,reason);
//        try {
//            re.Write(file_name,reason);//function+"_fail.log"
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    boolean is_error = false;

                    String real_version = Build.DISPLAY;
                    if (m_version.equals(real_version)) {
                        mTextResult.append(" OS version = " + real_version + "\t" + " ==> PASS !\n");
                    } else {
                        mTextResult.append(" OS version ( real , conf ) = " + "( " + real_version + " , " + m_version + " )" + " ==>" + fail_mesg + "\n");
                        is_error = true;
                    }

                    String sdk_version = Build.VERSION.RELEASE;
                    mTextResult.append("Android version = "+ sdk_version + "\t");
                    if(sdk_version.equals(os_version)){
                        mTextResult.append("==> PASS\n");
                    }else{
                        mTextResult.append("==> Fail.\n");
                        is_error = true;
                    }

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

}