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
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.msi.android.SysMgr;



public class TempActivity extends AppCompatActivity {

    private static final String TAG = TempActivity.class.getSimpleName();
    private boolean Debug = false;
    private TextView mTextResult;
    private TextView mTextTitle;
    private Button stop_btn;

    private int m_cpu_temp_max = -1;
    private int m_cpu_temp_min = -1;

    private int result = -1;
    private String mTestTemp = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

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

                    if (!checkCpuTemp()) {
                        is_error=true;
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
        mTextResult.append("\n\nTest "+ mTestTemp +" Pass!");
        mTextTitle.setText(mTestTemp +" Pass!");
        Intent intent2 = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(mTestTemp + "CPUTempResult", result);
        Log.d(TAG, "callpass: " + result);

        intent2.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent2);
        finish();

    }

    private void callfail() {
        mTextResult.append("\n\nTest "+ mTestTemp +" Fail!");
        mTextTitle.setText(mTestTemp +" Fail!");
//        writeLog(mTestInfo,mTextResult.getText().toString(),mTestInfo+"_fail.log");

        Intent intent2 = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(mTestTemp + "CPUTempResult", result);
        Log.d(TAG, "callfail: " + result);

        intent2.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent2);
        Message msg = mHandler.obtainMessage(1);
        mHandler.removeMessages(1);
        mHandler.sendMessageDelayed(msg, 2*1000);

    }

    private boolean checkCpuTemp() {
        String ls_return="";
        String ls_ver = SystemProperties.get("ro.build.version.release","4.4.2");
        String ls_dev = SystemProperties.get("ro.product.vendor.model","").toLowerCase();
        Log.d("VICTOR", "ls_dev = "+ ls_dev);
        String s = "";
        if (ls_ver.equals("4.4.2")) {
            s = SysMgr.nativeSetProp("excuteSystemCmdWithResult","cat /sys/class/thermal/thermal_zone0/temp").trim();
        } else {//4.4.3
            if (ls_dev.equals("ms5761") || ls_dev.equals("ms5761p") || ls_dev.equals("ms5766")) {
                Log.d("VICTOR", "project ....1 ");
                s = SysMgr.nativeSetProp("excuteSystemCmdWithResult","cat /sys/class/thermal/thermal_zone0/temp").trim();
            } else {
                Log.d("VICTOR", "project ....2 ");
                s = SysMgr.nativeSetProp("excuteSystemCmdWithResult","cat /sys/class/thermal/thermal_zone1/temp").trim();
            }
            if (!s.equals("") && !s.contains("Permission denied")) {
                Log.d("VICTOR", "project ....3 ");
                Log.d("CPU", "cat /sys/class/thermal/thermal_zone1/temp=>"+s);
                int i=Integer.parseInt(s)/1000;
                Log.d("CPU", "s/1000=>"+i);
                s=""+i;
            } else {
                Log.d("CPU", "cat /sys/class/thermal/thermal_zone1/temp=>"+s);
            }
        }

        if (s.equals("")) {
            ls_return="cpu temperature value is empty!";
            mTextResult.append("\n"+ls_return);
            return false;
        } else {
            int i = 0;
            try {
                i = Integer.parseInt(s);
            } catch (Exception e) {
                ls_return="cpu temperature value is not Integer!";
                mTextResult.append("\n"+ls_return);
                return false;
            }
            if (i > m_cpu_temp_max) {
                ls_return=getResources().getString(R.string.str_cpu_temperature) + s+ getResources().getString(R.string.str_cpu_temperature_unit)+
                        "(>"+m_cpu_temp_max+ getResources().getString(R.string.str_cpu_temperature_unit)+")"+
                        "\n\n"+getResources().getString(R.string.cpu_temp_hight);
                mTextResult.append("\n"+ls_return);
                return false;
            } else if (i < m_cpu_temp_min) {
                ls_return=getResources().getString(R.string.str_cpu_temperature) + s+ getResources().getString(R.string.str_cpu_temperature_unit)+
                        "(<"+m_cpu_temp_min+ getResources().getString(R.string.str_cpu_temperature_unit)+")"+
                        "\n\n"+getResources().getString(R.string.cpu_temp_low);
                mTextResult.append("\n"+ls_return);
                return false;
            } else if (i <= m_cpu_temp_max && i >= m_cpu_temp_min) {
                ls_return=getResources().getString(R.string.str_cpu_temperature) + s+ getResources().getString(R.string.str_cpu_temperature_unit);
                // pass
                mTextResult.append("\n"+ls_return);
                return true;
            }
        }
        ls_return="cpu temperature test fail!";
        mTextResult.append("\n"+ls_return);
        return false;
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