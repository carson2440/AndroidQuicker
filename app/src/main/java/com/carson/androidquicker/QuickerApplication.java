package com.carson.androidquicker;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.carson.androidquicker.api.DataService;
import com.carson.quicker.http.QHttpSocket;
import com.carson.quicker.logger.QLogger;
import com.carson.quicker.utils.QAndroid;
import com.carson.quicker.utils.QAppHandler;
import com.carson.quicker.utils.QStrings;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;

/**
 * Created by carson on 2018/3/9.
 */

public class QuickerApplication extends Application {

    static QuickerApplication instance;
    private DataService dataService;

    public static QuickerApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //support svg vector under android5.0
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        QLogger.builder().build();
        QAppHandler.with(this).create();
        QAndroid.enableStrictMode(this);

        initHttpSocket();
        printAppInfo();
    }

    private void printAppInfo() {
        String packageName = getPackageName();
        String versionName = QAndroid.getVersionName(this);
        int versionCode = QAndroid.getVersionCode(this);

        QLogger.debug("%s runnin mode with: %s", packageName, QAndroid.isDebug(this));
        QLogger.debug("version: %s - %s", versionName, versionCode);
    }


    /**
     * if server surppout cache  with  etag or cachecqontrol,cache will work will,otherwise, not work.
     */
    private void initHttpSocket() {
        Observable.just("AndroidQuicker")
                .map(s -> new Cache(QAndroid.getSDCard(s), 1024 * 1024 * 8))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cache -> {
                            dataService = QHttpSocket.with()
                                    .enableCache(cache)
//                                    .setHttpBuilder(null)
//                                    .setRetrofitBuilder(null)
                                    .setDebugMode(BuildConfig.DEBUG)
                                    .create("http://news-at.zhihu.com/api2/4/", DataService.class);
                        }
                );
    }

    public DataService getDataService() {
        if (QStrings.isEmpty(dataService)) {
            dataService = QHttpSocket.with()
                    .setDebugMode(QAndroid.isDebug(this))
//                    .setHttpBuilder()
//                    .setRetrofitBuilder()
                    .create("http://news-at.zhihu.com/api2/4/", DataService.class);
        }
        return dataService;
    }
}
