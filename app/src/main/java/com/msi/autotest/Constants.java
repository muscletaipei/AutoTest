package com.msi.autotest;

import android.app.Activity;

public class Constants {
    public static final int RETURN_CODE_FAIL = Activity.RESULT_CANCELED + 101;
    public static final int RETURN_CODE_SUCCESS = Activity.RESULT_OK;
    public static final String VERSION = "6.0.0";
    public static final String SDCARD_PATH = "/mnt/sdcard";
    public static final String Internal_Storage = "/data/data/com.msi.autotest/Autotest";
    static String CONF_FILE = "conf_N.xml";
    static String ITEM_CONF_FILE = "Item_conf_N.xml";
    static String PID_FILE = "pid_N.xml";

    public static void setConfName(String product_name) {
        if (product_name.equals("")) {
            CONF_FILE = "conf_N.xml";
            ITEM_CONF_FILE = "Item_conf_N.xml";
            PID_FILE = "pid_N.xml";
        } else {
            CONF_FILE = "conf_P_" + product_name + ".xml";
            ITEM_CONF_FILE = "Item_conf_P_" + product_name + ".xml";
            PID_FILE = "pid_P_" + product_name + ".xml";
        }
    }


}
