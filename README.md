# AndroidQuicker
AndroidQuicker is a powerful & easy to use common library for Android

* 监控APP运行Crash类：QAppHandler；在Application中oncreate（）方法中加入QAppHandler.with(this).create()开启监控.
* 日志打印类：QLogger；在Application中onCreate（）方法中加入QLogger.builder().build()
* 常用帮助类：在com.carson.quicker.utils空间下。（包含文件，网络，加解密，dp转换，String操作等帮助类）
* 网络访问类：QHttpSocket
* activity或fragment继承自QActivity或QFragment可以有效避免由于使用网络组建导致的内存泄露；

Usage
-----
**STEP 1**

Add the JitPack repository to your build file:
Add dependency to your gradle file:
```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

```groovy
implementation 'com.github.carson2440:AndroidQuicker:1.1.3'
```
**STEP 2**

Add the code where you want to use,such as the application class.
``` java
public class BaseApplication extends Application{
        ...
          @Override
            protected void onCreate() {
                ...
                QAndroid.enableStrictMode(this);
                QAppHandler.with(this).create();
                 Logger.builder()
                               .tag("carson")
                               .logLevel(Logger.DEBUG)
                               .logPolicy(2*1024*1024, 2)
                               .logfile(true, Environment.getExternalStorageDirectory().getPath() + "/download")
                               .expired(1)
                               .build();

                initHttpSocket();
                ...
            }

              private void initHttpSocket() {
            //        Storages.getExternalFilesDir(this, "HttpCache");
                    Observable.just("HttpCache")
                            .map(s -> new Cache(QStorages.getSDCard(s), 1024 * 1024 * 8))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(cache ->
                                    dataSource = QHttpSocket.with("http://news-at.zhihu.com/api/4/")
                                                                        .enableCache(cache)
                                                                    //  .setHttpBuilder(null)
                                                                    //  .setRetrofitBuilder(null)
                                                                        .setDebugMode(true)
                                                                        .create(DataSource.class);
                            );
                }
         ...
}
```

the DataSource.class is the api class.

``` java
public interface DataSource {
    //    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_SHORT)
    @GET("stories/latest")
    Observable<NewsList> getLatestNews();

    //  ===============================================================
    @FormUrlEncoded
    @POST("account/login")
    Observable<NewsList> login(
            @Field("userId") String userId,
            @Field("password") String password
    );

    @GET("video/getUrl")
    Observable<NewsList> getVideoUrl(
            @Query("id") long id
    );

    @FormUrlEncoded
    @POST("user/addVideo")
    Observable<NewsList> addVideo(
            @FieldMap Map<String, Object> map
    );

    @GET("stories/before/{date}")
    Observable<NewsList> getBeforeNews(@Path("date") String date);
}

```
**STEP 3**

call class：

``` java
  QuickerApplication.dataSource.getLatestNews().delay(5, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<NewsList>() {
             @Override
             public void onSubscribe(Disposable d) {
                 binding.loading.setVisibility(View.VISIBLE);
                 QLogger.d("onSubscribe");
             }

             @Override
             public void onNext(NewsList newsList) {
                 initData(new Gson().toJson(newsList));
                 QLogger.d("onNext" + new Gson().toJson(newsList));
             }

             @Override
             public void onError(Throwable e) {
                 QLogger.d("Throwable" + e);
                 initData("error:" + e.getMessage());
             }

             @Override
             public void onComplete() {
                 QLogger.d("onComplete");
             }
         });
 ```

 If you use rxlifecycle-components, just extend the appropriate class(such as Subactivity extends QActivity), then use the built-in bindToLifecycle() (or bindUntilEvent()) methods:


``` java

myObservable
    .compose(bindUntilEvent(lifecycle, ActivityEvent.DESTROY))
    .subscribe();


QuickerApplication.dataSource.getLatestNews().delay(5, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).compose(bindToLifecycle()).subscribe(new Observer<NewsList>() {
            @Override
            public void onSubscribe(Disposable d) {
                binding.loading.setVisibility(View.VISIBLE);
                QLogger.d("onSubscribe");
            }

            @Override
            public void onNext(NewsList newsList) {
                initData(new Gson().toJson(newsList));
                QLogger.d("onNext" + new Gson().toJson(newsList));
            }

            @Override
            public void onError(Throwable e) {
                QLogger.debug("Throwable" + e);
                initData("error:" + e.getMessage());
            }

            @Override
            public void onComplete() {
                QLogger.d("onComplete");
            }
        });

```

**That is all done!**

