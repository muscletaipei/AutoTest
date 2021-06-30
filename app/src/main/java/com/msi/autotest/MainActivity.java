package com.msi.autotest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
//    private static final int INFO_CODE = 100;
    private List<Function> functions;
    private boolean Debug = false;
    private int resultSystemInfo, resultMemory, resultRtc, resultCPUTemp, resultBattery, resultEmmc, resultReboot;
    File resultFile = new File(Environment.getExternalStorageDirectory(), "/Download/Auto_Test_Result.txt");

    private Context context;
    private TextView iconText;

    private String build_ver="";
    private String diag_ver="";
    private String mcu_ver="";

    private int Pass = 0;
    private int Fail = -1;
    //    String functions [] = null;
    private IconAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //避免螢幕自動關閉

        if (Debug) {
            Log.d(TAG, "onCreate" + Debug);
        }
        //設置標題欄
        String title = "Auto_Test_" + getCurrentVersion("com.msi.autotest");
        this.setTitle(title);

        context = getApplicationContext();

        //recycler
        setupFunctions();

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 6)); //設定Layout型態
        //adapter
//        FunctionAdapter adapter = new FunctionAdapter(this);
        adapter = new IconAdapter();
        recyclerView.setAdapter(adapter);

    }

    private void setupFunctions() {
        functions = new ArrayList<>();
        String[] funcs = getResources().getStringArray(R.array.functions);
        functions.add(new Function(funcs[0], R.drawable.func_info));
        functions.add(new Function(funcs[1], R.drawable.func_rtc));
        functions.add(new Function(funcs[2], R.drawable.func_memory));
        functions.add(new Function(funcs[3], R.drawable.func_temperature));
        functions.add(new Function(funcs[4], R.drawable.func_battery));
        functions.add(new Function(funcs[5], R.drawable.func_emmc));
        functions.add(new Function(funcs[6], R.drawable.func_sd));
        functions.add(new Function(funcs[7], R.drawable.func_usb));
        functions.add(new Function(funcs[8], R.drawable.func_wifi));
        functions.add(new Function(funcs[9], R.drawable.func_bluetooth));
        functions.add(new Function(funcs[10], R.drawable.func_fourg));
        functions.add(new Function(funcs[11], R.drawable.func_touch));
        functions.add(new Function(funcs[12], R.drawable.func_camera));
        functions.add(new Function(funcs[13], R.drawable.func_reboot1));
        functions.add(new Function(funcs[14], R.drawable.func_run));
        functions.add(new Function(funcs[15], R.drawable.func_log));
        functions.add(new Function(funcs[16], R.drawable.func_exit));


    }

    public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconHolder> {
        @NonNull
        @Override
        public IconHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_icon, parent, false);
            return new IconHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final IconHolder holder, int position) {
            Function function = functions.get(position);
            holder.iconText.setText(function.getName());
            holder.iconImage.setImageResource(function.getIcon());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClick(function);
                }
            });
            // 判斷iconText Pass or Fail
            if(function.getStatus() == 0) {
                holder.iconText.setTextColor(Color.rgb(255,0,0));
            } else if(function.getStatus() == 1) {
                holder.iconText.setTextColor(Color.rgb(0,0,255));
            } else {
                holder.iconText.setTextColor(Color.rgb(0,0,0));
            }
        }

        @Override
        public int getItemCount() {
            return functions.size();
        }

        public class IconHolder extends RecyclerView.ViewHolder {
            ImageView iconImage;
            TextView iconText;

            public IconHolder(@NonNull View itemView) {
                super(itemView);
                iconImage = itemView.findViewById(R.id.item_icon);
                iconText = itemView.findViewById(R.id.item_name);
            }
        }
    }

    private void itemClick(Function function) {
        Log.d(TAG, "itemClick:"  + "\t" + function.getName());
        switch (function.getIcon()) {
            case R.drawable.func_info:
                Intent info = new Intent(this, InfoActivity.class);
                startActivityForResult(info, 0);
                break;
//            case R.drawable.func_rtc:
//                Intent rtc = new Intent(this, RtcActivity.class);
//                startActivityForResult(rtc,1);
//                break;
            case R.drawable.func_memory:
                Intent memory = new Intent(this, MemoryActivity.class);
                startActivityForResult(memory, 2);
                break;
            case R.drawable.func_temperature:
                Intent temp = new Intent(this, TempActivity.class);
                startActivityForResult(temp, 3);
                break;
            case R.drawable.func_battery:
                Intent battery = new Intent(this, BatteryActivity.class);
                startActivityForResult(battery, 4);
                break;
            case R.drawable.func_emmc:
                Intent emmc = new Intent(this, EmmcActivity.class);
                startActivityForResult(emmc, 5);
                break;
            case R.drawable.func_sd:
                break;
            case R.drawable.func_usb:
                break;
            case R.drawable.func_wifi:
                Intent wifi = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=NbjI0cARzjQ"));
                startActivity(wifi);
                break;
            case R.drawable.func_bluetooth:
                break;
            case R.drawable.func_fourg:
                break;
            case R.drawable.func_touch:
                break;
            case R.drawable.func_camera:
                break;
            case R.drawable.func_reboot1:
                Intent reboot = new Intent(this, RebootActivity.class);
                startActivityForResult(reboot, 13);
                break;
            case R.drawable.func_run:
                break;
            case R.drawable.func_log:
                break;
            case R.drawable.func_exit:
                new AlertDialog.Builder(this)
                        .setTitle("Message")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("Are you sure to closed the Application?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode:" + "---------" + "\t" + requestCode);
        Log.d(TAG, "onActivityResult: resultCode:" + "---------" + "\t" + resultCode);

        iconText = findViewById(R.id.item_name);

        switch (requestCode) {
            case 0:
                resultSystemInfo = data.getExtras().getInt("Pass");
                Log.d(TAG, "resultSystemInfo ------------------------get \t" + resultSystemInfo);
                if (requestCode == 0){
                    functions.get(0).setStatus(resultSystemInfo); // directly point to an icon
                    adapter.notifyDataSetChanged(); //renew status
                }
                break;
            case 1:
                resultRtc = data.getExtras().getInt("RTCResult");
                Log.d(TAG, "resultRtc ---------------------get \t " + resultRtc);
                if (requestCode == 1){
                    functions.get(1).setStatus(resultRtc);
                    adapter.notifyDataSetChanged();
                }
                break;
            case 2:
                resultMemory = data.getExtras().getInt("MemoryResult");
                Log.d(TAG, "MemoryResult ------------------------get \t" + resultMemory);
                if (requestCode == 2){
                    functions.get(2).setStatus(resultMemory);
                    adapter.notifyDataSetChanged();
                }
                break;
            case 3:
                resultCPUTemp = data.getExtras().getInt("CPUTempResult");
                Log.d(TAG, "TempResult ------------------------get \t" + resultCPUTemp);
                if (requestCode == 3){
                    functions.get(3).setStatus(resultCPUTemp);
                    adapter.notifyDataSetChanged();
                }
                break;
            case 4:
                resultBattery = data.getExtras().getInt("BatteryPass");
                Log.d(TAG, "BatteryResult ------------------------get \t" + resultBattery);
                if (requestCode == 4){
                    functions.get(4).setStatus(resultBattery);
                    adapter.notifyDataSetChanged();
                }
                break;
            case 5:
                resultEmmc = data.getExtras().getInt("eMMCResult");
                Log.d(TAG, "resultEmmc ------------------------get \t" + resultEmmc);
                if (requestCode == 5){
                    functions.get(5).setStatus(resultEmmc);
                    adapter.notifyDataSetChanged();
                }
                break;
            case 13:
                resultReboot = data.getExtras().getInt("rebootResult");
                Log.d(TAG, "resultReboot ------------------------get \t" + resultReboot);
                if (requestCode == 13){
                    functions.get(13).setStatus(resultReboot);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }


    private String getCurrentVersion(String packageName) {
        try {
            PackageManager packageManager = getPackageManager();
            try {
                PackageInfo info = packageManager.getPackageInfo(packageName, 0);
                if (Debug) {
                    Log.d(TAG, "Version:" + info.versionName);
                }
                return info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                return "unknown !";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "unknown !";
        }
    }


}
