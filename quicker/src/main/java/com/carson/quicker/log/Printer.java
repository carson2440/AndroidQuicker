package com.carson.quicker.log;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @author yanxu QQ:981385016
 * @name QLogger
 * @class name：com.skyguard.carson.logger
 * @time 2018/10/17 18:23
 * @desc describe
 */
public interface Printer {

    void addAdapter(@NonNull PrinterAdapter adapter);

    void clearLogAdapters();

    Printer tag(@Nullable String tag);

    void v(@NonNull String message, @Nullable Object... args);

    void d(@NonNull String message, @Nullable Object... args);

    void d(@Nullable Object object);

    void i(@NonNull String message, @Nullable Object... args);

    void w(@NonNull String message, @Nullable Object... args);

    void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args);

    void f(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args);

    void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable throwable);
}
