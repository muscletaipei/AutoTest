package com.msi.autotest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RebootActivity extends AppCompatActivity {

    private static final String TAG = RebootActivity.class.getSimpleName();
    private boolean Debug = false;
    private Button reboot_button;

    Process process = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reboot);

        if(Debug){
            Log.d(TAG,"onCreate" + Debug);
        }
        //設置標題欄
        String title = "Auto_Test_" + getCurrentVersion("com.msi.autotest");
        this.setTitle(title);

        reboot_button = findViewById(R.id.reboot_button);
        reboot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                process = Runtime.getRuntime().exec("reboot");
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
}