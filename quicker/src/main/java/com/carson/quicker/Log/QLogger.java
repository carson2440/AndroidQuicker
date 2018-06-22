package com.carson.quicker.Log;

import android.util.Log;

import com.carson.quicker.utils.QStrings;

/**
 * Created by carson on 2018/3/9.
 */

public class QLogger {
    /**
     * 在androidstudio3.0.1中,华为android自带Log默认无法输出Log.d(),Log.i(),Log.v().
     * 需要单独设置开启
     */
    private static String APP_TAG = "quicker";
    private static boolean printDebugLog = false;

    public static void init(boolean debug) {
        printDebugLog = debug;
    }

    public static void init(boolean debug, String globalTag) {
        printDebugLog = debug;
        if (QStrings.isNotBlank(globalTag)) {
            APP_TAG = globalTag;
        }
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

    public static void error(Exception e) {
        if (e != null) {
            Log.e(APP_TAG, e.getMessage());
        }
    }
}
