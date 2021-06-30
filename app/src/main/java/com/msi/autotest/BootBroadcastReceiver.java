package com.msi.autotest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.msi.autotest.MainActivity;


public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Intent serviceIntent = new Intent(context,MainActivity.class);
            context.startService(serviceIntent);

            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);



        }
    }
}