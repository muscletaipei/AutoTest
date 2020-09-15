package com.msi.autotest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MemoryActivity extends AppCompatActivity {

    private static final String TAG = MemoryActivity.class.getSimpleName();
    private boolean Debug = false;
    private TextView mTextResult;
    private TextView mTextTitle;
    private Button stop_btn;

    private int result = -1;

    private String mTestMemory = "";
    private int m_memory_size_max = 2060000;
    private int m_memory_size_min = 2040000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

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

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    boolean is_error = false;

                    Log.d(TAG, "is_error----------" + is_error);
                    if (!checkMemorySize()) {
                        is_error = true;
                    }
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
        mTextResult.append("\n\nTest "+ mTestMemory +" Pass!");
        mTextTitle.setText(mTestMemory +" Pass!");
        Intent intent2 = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(mTestMemory + "MemoryResult", result);
        Log.d(TAG, "callpass: " + result);

        intent2.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent2);
        finish();

    }

    private void callfail() {
        mTextResult.append("\n\nTest "+ mTestMemory +" Fail!");
        mTextTitle.setText(mTestMemory +" Fail!");
//        writeLog(mTestInfo,mTextResult.getText().toString(),mTestInfo+"_fail.log");

        Intent intent2 = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(mTestMemory + "MemoryResult", result);
        Log.d(TAG, "callfail: " + result);

        intent2.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent2);
        Message msg = mHandler.obtainMessage(1);
        mHandler.removeMessages(1);
        mHandler.sendMessageDelayed(msg, 2*1000);

    }

    private boolean checkMemorySize() {
        String ls_return="";
        String s = cmd_exec("cat /proc/meminfo");
        String s1= "";
        if (s.equals("")) {
            ls_return="meminfo is empty!";
            mTextResult.append("\n"+ls_return);
            return false;
        } else {
            int i = 0;
            try {
                //MemTotal:         821816 kB=>4.4.2
                //MemTotal:        1016512 kB=>4.4.3
                int index1=s.indexOf(":");
                int index2=s.indexOf("kB");
                if (index1 == -1 || index2 == -1) {
                    return false;
                }
                s1=s.substring(index1+1, index2);
                //i = Integer.parseInt(s);
                Log.d("Memory", "s1:"+s1);
                i=Integer.parseInt(s1.trim());
            } catch (Exception e) {
                ls_return ="Memory value is not Integer!";
                mTextResult.append("\n"+ls_return);
                return false;
            }
            if (i >= m_memory_size_max) {
                ls_return=getResources().getString(R.string.str_memory_size) + s1+ getResources().getString(R.string.str_memory_size_unit)+
                        "(>"+m_memory_size_max+getResources().getString(R.string.str_memory_size_unit)+")"+
                        "\n\n"+getResources().getString(R.string.memory_size_large);
                mTextResult.append("\n"+ls_return);
                return false;
            } else if (i < m_memory_size_min) {
                ls_return=getResources().getString(R.string.str_memory_size) + s1+ getResources().getString(R.string.str_memory_size_unit)+
                        "(<"+m_memory_size_min+getResources().getString(R.string.str_memory_size_unit)+")"+
                        "\n\n"+getResources().getString(R.string.memory_size_small);
                mTextResult.append("\n"+ls_return);
                return false;
            } else if (i < m_memory_size_max && i >= m_memory_size_min) {
                ls_return=getResources().getString(R.string.str_memory_size) + s1+ getResources().getString(R.string.str_memory_size_unit);
                mTextResult.append("\n"+ls_return);
                return true; // pass
            }
        }
        ls_return="Memory test fail!";
        mTextResult.append("\n"+ls_return);
        return false;
    }
    private String cmd_exec(String ls_cmd) {
        String ls_return = "";
        try {
            Process pr = Runtime.getRuntime().exec(ls_cmd);
            InputStream rs = pr.getInputStream();
            InputStreamReader isr = new InputStreamReader(rs);
            BufferedReader bfr = new BufferedReader(isr);
            StringBuffer sbf = new StringBuffer();
            String tmp = "";

            while ((tmp = bfr.readLine()) != null) {
                sbf.append(tmp);
                break;
            }
            if (sbf != null) {
                ls_return = sbf.toString();
            }
        } catch (Exception e) {

        }
        return ls_return;
    }
}