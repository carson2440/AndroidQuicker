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


    private final Executor mDiskIO = Executors.newSingleThreadExecutor();
    private final MainThreadExecutor mMainThread = new MainThreadExecutor();

    private QExecutors() {
    }

    private static class QExecutorsHandler {
        private static final QExecutors instance = new QExecutors();
    }

    public static final QExecutors with() {
        return QExecutorsHandler.instance;
    }


    public Executor threadIO() {
        return mDiskIO;
    }

    public MainThreadExecutor threadMain() {
        return mMainThread;
    }

    private class MainThreadExecutor implements Executor {
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
