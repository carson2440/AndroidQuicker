package com.carson.quicker.log;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import static com.carson.quicker.log.Utils.checkNotNull;

/**
 * @author yanxu QQ:981385016
 * @name QLogger
 * @class name：com.skyguard.carson.logger
 * @time 2018/10/17 18:10
 * @desc describe
 */
public final class QLogger {

    /**
     * 在androidstudio3.0.1中,华为android自带Log默认无法输出Log.d(),Log.i(),Log.v().
     * 需要单独设置开启
     */

    public static final int FATAL = Log.ASSERT;
    public static final int ERROR = Log.ERROR;
    public static final int WARN = Log.WARN;
    public static final int INFO = Log.INFO;
    public static final int DEBUG = Log.DEBUG;
    public static final int TRACE = Log.VERBOSE;

    @NonNull
    private static Printer printer = new LoggerPrinter();
    private static volatile Builder builder;
    static FileLogAdapter adapter;


    public interface OnZipListener {
        void onZip(boolean succeed, String target);
    }

    private QLogger() {
        //no instance
    }

    private static void addLogAdapter(@NonNull PrinterAdapter adapter) {
        printer.addAdapter(checkNotNull(adapter));
    }

    private static void clearLogAdapters() {
        printer.clearLogAdapters();
    }

    public static void zipLogs(OnZipListener listener) {
        if (adapter == null) {
            QLogger.w("skip zip log file , logfile is disable");
        } else if (listener != null) {
            adapter.zipLogs(listener);
        }
    }

    private static Printer tag(@Nullable String tag) {
        return printer.tag(tag);
    }

    private static String getStackTace() {
        StackTraceElement[] element = Thread.currentThread().getStackTrace();
        StackTraceElement a = element[4];
        String info = String.format("%s:%s [%s] ", a.getFileName(), a.getLineNumber(), Thread.currentThread().getId());
        StringBuilder builder = new StringBuilder();
        if (info.length() < 40) {
            builder.append(info);
            for (int i = 0; i < 40 - info.length(); i++) {
                builder.append(" ");
            }
        } else {
            builder.append(info.substring(0, 40));
        }
        return builder.toString();
    }

    private static String tagFormat(String tag, String message) {
        if (Utils.isEmpty(tag)) {
            return message;
        } else {
            return tag + " : " + message;
        }
    }

    public static void v(@NonNull String message, @Nullable Object... args) {
        message = getStackTace() + message;
        printer.v(null, message, args);
    }

    public static void v(@Nullable String tag, @NonNull String message, @Nullable Object... args) {
        message = getStackTace() + tagFormat(tag, message);
        printer.v(message, args);
    }

    public static void d(@NonNull String message, @Nullable Object... args) {
        message = getStackTace() + message;
        printer.d(null, message, args);
    }

    public static void d(@Nullable String tag, @NonNull String message, @Nullable Object... args) {
        message = getStackTace() + tagFormat(tag, message);
        printer.d(message, args);
    }

    public static void d(@Nullable Object object) {
        if (object != null && object instanceof String) {
            String message = getStackTace() + object.toString();
            printer.d(message);
        } else {
            printer.d(object);
        }
    }

    public static void i(@NonNull String message, @Nullable Object... args) {
        message = getStackTace() + message;
        printer.i(message, args);
    }

    public static void i(@Nullable String tag, @NonNull String message, @Nullable Object... args) {
        message = getStackTace() + tagFormat(tag, message);
        printer.i(message, args);
    }

    public static void w(@NonNull String message, @Nullable Object... args) {
        message = getStackTace() + message;
        printer.w(message, args);
    }

    public static void w(@Nullable String tag, @NonNull String message, @Nullable Object... args) {
        message = getStackTace() + tagFormat(tag, message);
        printer.w(message, args);
    }


    public static void e(@NonNull String message, @Nullable Object... args) {
        message = getStackTace() + message;
        printer.e(null, message, args);
    }

    public static void e(@Nullable String tag, @NonNull String message, @Nullable Object... args) {
        message = getStackTace() + tagFormat(tag, message);
        printer.e(null, message, args);
    }

    public static void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        message = getStackTace() + message;
        printer.e(throwable, message, args);
    }

    /**
     * Formats the given json content and print it
     */
    public static void json(@Nullable String json) {
        printer.json(json);
    }

    public static Builder builder() {
        if (builder == null) {
            synchronized (QLogger.class) {
                if (builder == null) {
                    builder = new Builder();
                }
            }
        }
        return builder;
    }

    private static void build(Builder builder) {
        if (builder.isCatchAppException) {
            CrashWatcher.getInstance().init();
            CrashWatcher.getInstance().setListener(builder.listener);
        } else {
            CrashWatcher.getInstance().setListener(null);
        }
        tag(builder.tag);
        clearLogAdapters();
        if (builder.enableLogcat) {
            addLogAdapter(new AndroidLogAdapter(builder.logLevel));
        }
        if (builder.enableLogfile) {
            adapter = new FileLogAdapter(builder.logLevel, builder.tag);
            adapter.initFileLogger(builder.fileDirectory, builder.expiredPeriod, builder.fileLimit, builder.fileCount);
            addLogAdapter(adapter);
        }
    }

    public static final class Builder {
        protected String tag = QLogger.class.getSimpleName();
        private boolean isCatchAppException = false;
        private boolean enableLogfile = false;
        private boolean enableLogcat = true;
        protected int expiredPeriod = 1;
        protected String fileDirectory;
        private int fileLimit = 3 * 1024 * 1024;
        private int fileCount = 3;
        private CrashWatcher.CrashListener listener;
        protected int logLevel = QLogger.TRACE;

        public void build() {
            QLogger.build(this);
        }

        public Builder() {
            listener = new CrashWatcher.CrashListener() {
                @Override
                public void onAppCrash(Thread thread, Throwable ex) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            };
        }

        public Builder tag(String tag) {
            if (!TextUtils.isEmpty(tag)) {
                this.tag = tag;
            }
            return this;
        }

        public Builder logLevel(int level) {
            if (level >= QLogger.TRACE && level <= QLogger.FATAL) {
                this.logLevel = level;
            }
            return this;
        }

        public Builder logcat(boolean enable) {
            this.enableLogcat = enable;
            return this;
        }

        public Builder logfile(boolean enable, String path) {
            this.enableLogfile = enable;
            if (TextUtils.isEmpty(path)) {
                throw new IllegalArgumentException("fileLogger path cann't null");
            }
            File filePath = new File(path);
            if (!filePath.exists() && !filePath.mkdirs()) {
                QLogger.e(tag, "QLogger enable failed, please check permission: cann't make dir.");
            }
            this.fileDirectory = path;
            return this;
        }

        public Builder logPolicy(int limit, int count) {
            this.fileLimit = limit;
            this.fileCount = count;
            return this;
        }

        public Builder crashEvent(boolean enable, CrashWatcher.CrashListener l) {
            this.isCatchAppException = enable;
            this.listener = l;
            return this;
        }

        /**
         * set the period of file expired
         *
         * @throws IllegalArgumentException if the period <= 0
         */
        public Builder expired(int period) {
            if (period <= 0) {
                throw new IllegalArgumentException("unexpected period : " + period);
            }
            expiredPeriod = period;
            return this;
        }

    }
}
