package com.carson.quicker.utils;

import android.app.Application;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import com.carson.quicker.Log.QLogger;
import com.carson.quicker.QExecutors;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by carson on 2018/3/9.
 * App crash 监控
 * QAppHandler.with(this).create()
 */

public class QAppHandler implements Thread.UncaughtExceptionHandler {
    private Application application;
    private Thread.UncaughtExceptionHandler defaultHandler;
    private File crashLogPath;

    private static final class MonitorsHolder {
        private static final QAppHandler MONITORS = new QAppHandler();
    }

    public static final QAppHandler with(Application application) {
        MonitorsHolder.MONITORS.application = application;
        return MonitorsHolder.MONITORS;
    }

    public void create() {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        QExecutors.init().threadIO().execute(new Runnable() {
            @Override
            public void run() {
                crashLogPath = QStorages.getCacheDir(application, "log");
            }
        });
    }

    private void printLogIfDebug(Thread t, Throwable e) {
        if (QAndroid.isDebug(application)) {
            if (t != null) {
                StringBuilder builder = new StringBuilder(t.toString());
                builder.append(" ID = " + t.getId());
                QLogger.error(builder.toString());
            }
            e.printStackTrace();
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        printLogIfDebug(t, e);
        if (monitorException(t, e)) {
            try {
                Thread.sleep(1600);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(t, e);
            }
        }
    }

    private boolean monitorException(Thread thread, Throwable throwable) {
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(application, "APP正在退出, 请稍后...", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        if (throwable != null) {
            try {
                File log = new File(this.crashLogPath, "crash.log");
                if (QStorages.mkdirs(this.crashLogPath)) {
                    FileWriter fw = new FileWriter(log);
                    fw.write(throwable.toString() + "\r\n");
                    StackTraceElement[] stackTrace = throwable.getStackTrace();
                    for (int i = 0; i < stackTrace.length; i++) {
                        if (stackTrace[i].getClassName().contains(application.getPackageName())) {
                            fw.write(stackTrace[i].getFileName() + " line:" + stackTrace[i].getLineNumber() + " class:"
                                    + stackTrace[i].getClassName() + " method:" + stackTrace[i].getMethodName() + "\r\n");
                        }
                    }
                    fw.write("\r\n");
                    fw.write(Build.MODEL + "\t" + Build.HARDWARE + "\t" + Build.VERSION.RELEASE + "(" + Build.VERSION.SDK_INT
                            + ")\t" + QAndroid.getVersionName(application) + "-" + QAndroid.getVersionCode(application));
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
}
