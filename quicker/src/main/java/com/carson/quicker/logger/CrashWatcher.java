package com.carson.quicker.logger;

/**
 * @author yanxu QQ:981385016
 * @name QLogger
 * @class name：com.skyguard.carson.log
 * @time 2018/10/18 11:07
 * @desc describe
 */
public class CrashWatcher implements Thread.UncaughtExceptionHandler {
    private static CrashWatcher mInstance = new CrashWatcher();
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private CrashListener listener;

    private CrashWatcher() {
    }

    public static CrashWatcher getInstance() {
        return mInstance;
    }

    public void init() {
        if (null != mDefaultHandler)
            return;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void setListener(CrashListener listener) {
        this.listener = listener;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (null == listener) {

        } else {
            listener.onAppCrash(thread, ex);
        }
    }

    public interface CrashListener {
        void onAppCrash(Thread thread, Throwable ex);
    }
}

