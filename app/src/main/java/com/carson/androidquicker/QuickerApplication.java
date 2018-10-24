package com.carson.androidquicker;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.carson.androidquicker.api.DataSource;
import com.carson.quicker.log.QLogger;
import com.carson.quicker.http.QHttpSocket;
import com.carson.quicker.utils.QAndroid;
import com.carson.quicker.utils.QAppHandler;
import com.carson.quicker.utils.QStorages;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;

/**
 * Created by carson on 2018/3/9.
 */

public class QuickerApplication extends Application {
    public static DataSource dataSource;

    @Override
    public void onCreate() {
        super.onCreate();
        //support svg vector under android5.0
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        QLogger.builder().build();
        QAppHandler.with(this).create();
        QAndroid.enableStrictMode(this);

        initHttpSocket();
    }


    /**
     * if server surppout cache  with  etag or cachecontrol,cache will work will,otherwise, not work.
     */
    private void initHttpSocket() {
//        Storages.getExternalFilesDir(this, "HttpCache");

        Observable.just("HttpCache")
                .map(s -> new Cache(QStorages.getSDCard(s), 1024 * 1024 * 8))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cache -> {
                            dataSource = QHttpSocket.with("http://news-at.zhihu.com/api2/4/")
                                    .enableCache(cache)
//                                    .setHttpBuilder(null)
//                                    .setRetrofitBuilder(null)
                                    .setDebugMode(BuildConfig.DEBUG)
                                    .create(DataSource.class);
                        }
                );
    }
}
