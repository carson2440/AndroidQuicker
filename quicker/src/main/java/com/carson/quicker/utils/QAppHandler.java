package com.carson.quicker.utils;

import android.app.Application;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import com.carson.quicker.log.QLogger;
import com.carson.quicker.QExecutors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by carson on 2018/3/9.
 * App crash 监控
 * QAppHandler.with(this).create()
 */

public class QAppHandler implements Thread.UncaughtExceptionHandler {
    private Application application;
    private Thread.UncaughtExceptionHandler exceptionHandler;
    private File baseLogPath;
    private boolean retry;

    private static final class MonitorsHolder {
        private static final QAppHandler MONITORS = new QAppHandler();
    }

    public static final QAppHandler with(Application application) {
        MonitorsHolder.MONITORS.application = application;
        return MonitorsHolder.MONITORS;
    }

    public void create() {
        exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        QExecutors.with().threadIO().execute(new Runnable() {
            @Override
            public void run() {
                baseLogPath = QStorages.getCacheDir(application, "log");
            }
        });
    }

    public void destroy() {
        MonitorsHolder.MONITORS.application = null;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (processException(t, e)) {
            try {
                Thread.sleep(1600);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            if (exceptionHandler != null && retry) {
                exceptionHandler.uncaughtException(t, e);
            }
        }
    }

    private boolean processException(Thread thread, Throwable throwable) {
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(application, "APP正在退出, 请稍后...", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        String mobileInfo = Build.MODEL + "\t" + Build.HARDWARE + "\t" + Build.VERSION.RELEASE + ":" + Build.VERSION.SDK_INT +
                "\t" + QAndroid.getVersionName(application);
        QLogger.e(throwable, mobileInfo);
        if (throwable != null) {
            try {
                File log = new File(this.baseLogPath, "crash.log");
                if (QStorages.mkdirs(this.baseLogPath)) {
                    FileWriter fw = new FileWriter(log);
                    fw.write(thread.getId() + thread.getName() + ": " + throwable.toString() + "\r\n");
                    StackTraceElement[] stackTrace = throwable.getStackTrace();
                    for (int i = 0; i < stackTrace.length; i++) {
                        if (stackTrace[i].getClassName().contains(application.getPackageName())) {
                            fw.write(stackTrace[i].getFileName() + " line:" + stackTrace[i].getLineNumber() + " class:"
                                    + stackTrace[i].getClassName() + " method:" + stackTrace[i].getMethodName() + "\r\n");
                        }
                    }
                    fw.write("\r\n");
                    fw.write(mobileInfo);
                    fw.flush();
                    fw.close();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void cacheDebugLog(String message) {
        try {
            File dstFile = new File(this.baseLogPath, "debug.log");
            if (QStorages.mkdirs(dstFile.getParentFile())) {
                FileWriter fw = new FileWriter(dstFile, true);

                fw.write(QAndroid.getUnixTime("MM-dd HH:mm:ss"));
                fw.write(": ");
                fw.write(message);
                fw.write("\r\n");
                fw.flush();
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
