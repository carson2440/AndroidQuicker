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
    private final MainThreadExecutor mMainThread;

    private QExecutors() {
        this(Executors.newSingleThreadExecutor(), new MainThreadExecutor());
    }

    private QExecutors(Executor diskIO, MainThreadExecutor mainThread) {
        this.mDiskIO = diskIO;
        this.mMainThread = mainThread;
    }


    public static final QExecutors init() {
        return QExecutorsHandler.instance;
    }


    public Executor threadIO() {
        return mDiskIO;
    }

    public MainThreadExecutor threadMain() {
        return mMainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }

        public void executeDelayed(@NonNull Runnable command, int delayMillis) {
            mainThreadHandler.postDelayed(command, delayMillis);
        }
    }
}
