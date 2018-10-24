package com.carson.quicker.log;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.carson.quicker.log.Utils.checkNotNull;


/**
 * @author yanxu QQ:981385016
 * @name QLogger
 * @class name：com.skyguard.carson.logger
 * @time 2018/10/17 18:30
 * @desc describe
 */
class LoggerPrinter implements Printer {
    private String appGlobalTag = QLogger.class.getSimpleName();

    private final List<PrinterAdapter> logAdapters = new ArrayList<>();

    @Override
    public void addAdapter(@NonNull PrinterAdapter adapter) {
        logAdapters.add(checkNotNull(adapter));
    }

    @Override
    public void clearLogAdapters() {
        logAdapters.clear();
    }

    @Override
    public Printer tag(@Nullable String tag) {
        this.appGlobalTag = tag;
        return this;
    }

    @Override
    public void v(@NonNull String message, @Nullable Object... args) {
        log(QLogger.TRACE, null, message, args);
    }

    @Override
    public void d(@NonNull String message, @Nullable Object... args) {
        log(QLogger.DEBUG, null, message, args);
    }

    @Override
    public void d(@Nullable Object object) {
        log(QLogger.DEBUG, null, Utils.toString(object));
    }

    @Override
    public void i(@NonNull String message, @Nullable Object... args) {
        log(QLogger.INFO, null, message, args);
    }

    @Override
    public void w(@NonNull String message, @Nullable Object... args) {
        log(QLogger.WARN, null, message, args);
    }

    @Override
    public void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        log(QLogger.ERROR, throwable, message, args);
    }

    @Override
    public void f(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        log(QLogger.FATAL, throwable, message, args);
    }

    /**
     * It is used for json pretty print
     */
    private static final int JSON_INDENT = 2;

    @Override
    public void json(@Nullable String json) {
        if (Utils.isEmpty(json)) {
            d("QLogger.json(json) Empty/Null json");
            return;
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String message = jsonObject.toString(JSON_INDENT);
                d(message);
                return;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String message = jsonArray.toString(JSON_INDENT);
                d(message);
                return;
            }
            e(null, "Invalid Json");
        } catch (JSONException e) {
            e(null, "Invalid Json");
        }
    }

    @Override
    public void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable throwable) {
        if (Utils.isEmpty(message)) {
            message = "";
        }
        for (PrinterAdapter adapter : logAdapters) {
            if (adapter.isLoggable(priority, tag)) {
                adapter.log(priority, tag, message, throwable);
            }
        }
    }

    private void log(int priority,
                     @Nullable Throwable throwable,
                     @NonNull String msg,
                     @Nullable Object... args) {
        checkNotNull(msg);

        String message = createMessage(msg, args);
        log(priority, getTag(), message, throwable);
    }

    /**
     * @return the appropriate tag based on local or global
     */
    @Nullable
    private String getTag() {
        if (Utils.isEmpty(appGlobalTag)) {
            return QLogger.class.getSimpleName();
        } else {
            return appGlobalTag;
        }
    }

    @NonNull
    private String createMessage(@NonNull String message, @Nullable Object... args) {
        return args == null || args.length == 0 ? message : String.format(message, args);
    }
}
