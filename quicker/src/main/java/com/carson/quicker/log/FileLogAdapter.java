package com.carson.quicker.log;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.carson.quicker.log.formatter.FileFormatter;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author yanxu QQ:981385016
 * @name QLogger
 * @class name：com.skyguard.carson.log
 * @time 2018/10/18 15:57
 * @desc describe
 */
public class FileLogAdapter implements PrinterAdapter {

    private java.util.logging.Logger logger;
    private int logLevel;
    private int expiredPeriod;
    private HandlerThread fileLoggerThread;
    private Handler handler;

    private String logDir;

    public FileLogAdapter(int logLevel, String tag) {
        this.logLevel = logLevel;
        fileLoggerThread = new HandlerThread("thread-loggerFile");
        fileLoggerThread.start();
        handler = new Handler(fileLoggerThread.getLooper());
        this.logger = java.util.logging.Logger.getLogger(tag);
        logger.setUseParentHandlers(false);
    }

    @Override
    public boolean isLoggable(int priority, @Nullable String tag) {
        return priority >= this.logLevel;
    }

    @Override
    public void log(final int priority, @NonNull String tag, @NonNull final String message, @Nullable final Throwable throwable) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                log(getLoggerLevel(priority), message, throwable);
            }
        });
    }

    public Level getLoggerLevel(int logLevel) {
        Level level = Level.ALL;
        switch (logLevel) {
            case QLogger.TRACE:
                level = Level.ALL;
                break;
            case QLogger.DEBUG:
                level = Level.FINE;
                break;
            case QLogger.INFO:
                level = Level.INFO;
                break;
            case QLogger.WARN:
                level = Level.WARNING;
                break;
            case QLogger.ERROR:
                level = Level.SEVERE;
                break;
            case QLogger.FATAL:
                level = Level.SEVERE;
                break;
            default:
        }
        return level;
    }

    public FileLogAdapter initFileLogger(String path, int expired, int limit, int number) {
        this.logDir = path;
        this.expiredPeriod = expired;

        if (!logDir.endsWith("/"))
            logDir += "/";
        logger.setLevel(getLoggerLevel(logLevel));

        File file = new File(logDir + Utils.getCurrentDate());
        FileHandler fh;
        try {
            fh = new FileHandler(file.toString(), limit, number, true);
//            fh.setFormatter(new SimpleFormatter());
            fh.setFormatter(new FileFormatter());
            logger.addHandler(fh);

        } catch (IOException e) {
            //unused
            if (logger.isLoggable(Level.SEVERE)) {
                log(Level.SEVERE, logger.getName(), e);
            }
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                Utils.getSuitableFilesWithClear(logDir, expiredPeriod);
            }
        });
        return this;
    }

    private synchronized void log(Level level, String msg, Throwable t) {
        LogRecord record = new LogRecord(level, msg);
        record.setLoggerName(logger.getName());
        record.setThrown(t);
        logger.log(record);
    }

    public void zipLogs(final QLogger.OnZipListener listener) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                boolean result = false;
                String targetZipFileName = logDir + Utils.getCurrentDate() + Utils.SUFFIX_ZIP;
                try {
                    File zipFile = new File(targetZipFileName);
                    if (zipFile.exists() && !zipFile.delete())
                        QLogger.error("can not delete exist zip file!");
                    result = Utils.zipFiles(Utils.getSuitableFilesWithClear(logDir, expiredPeriod),
                            zipFile, Utils.getCurrentDate());
                } catch (Exception e) {
                    QLogger.e(e, "zip log exception.");
                }
                if (null != listener)
                    listener.onZip(result, targetZipFileName);
            }
        });
    }


}
