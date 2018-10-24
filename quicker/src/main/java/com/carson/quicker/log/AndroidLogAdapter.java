package com.carson.quicker.log;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @author yanxu QQ:981385016
 * @name QLogger
 * @class name：com.skyguard.carson.log
 * @time 2018/10/18 11:42
 * @desc describe
 */
public class AndroidLogAdapter implements PrinterAdapter {
    private int logLevel;

    public AndroidLogAdapter(int level) {
        logLevel = level;
    }

    @Override
    public boolean isLoggable(int priority, @Nullable String tag) {
        return priority >= this.logLevel;
    }

    @Override
    public void log(int priority, @NonNull String tag, @NonNull String message, @Nullable Throwable throwable) {
        if (throwable != null) {
            message += " : " + Utils.getStackTraceString(throwable);
        }
        Log.println(priority, tag, message);
    }


}
