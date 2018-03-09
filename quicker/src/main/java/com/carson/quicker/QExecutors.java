package com.carson.quicker;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by carson on 2018/3/9.
 */

public class QExecutors {

    private static class QExecutorsHandler {
        private static final QExecutors instance = new QExecutors();
    }

    private final Executor mDiskIO;
    private final Executor mMainThread;

    private QExecutors() {
        this(Executors.newSingleThreadExecutor(), new MainThreadExecutor());
    }

    private QExecutors(Executor diskIO, Executor mainThread) {
        this.mDiskIO = diskIO;
        this.mMainThread = mainThread;
    }


    public static final QExecutors builder() {
        return QExecutorsHandler.instance;
    }


    public Executor threadIO() {
        return mDiskIO;
    }

    public Executor threadMain() {
        return mMainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
