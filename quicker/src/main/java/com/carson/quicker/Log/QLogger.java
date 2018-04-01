package com.carson.quicker.Log;

import android.util.Log;

/**
 * Created by carson on 2018/3/9.
 */

public class QLogger {
    /**
     * 在androidstudio3.0.1中,华为android自带Log默认无法输出Log.d(),Log.i(),Log.v().
     * 需要单独设置开启
     */
    private static final String APP_TAG = "QLogger";
    private static boolean printDebugLog = true;

    public static void setDebugMode(boolean debug) {
        printDebugLog = debug;
    }

    public static void debug(String msg) {
        debug(APP_TAG, msg);
    }

    public static void error(String msg) {
        error(APP_TAG, msg);
    }

    public static void debug(String tag, String msg) {
        if (printDebugLog) {
            Log.i(tag, msg);
        }
    }

    public static void error(String tag, String msg) {
        if (printDebugLog) {
            Log.e(tag, msg);
        }
    }
}
