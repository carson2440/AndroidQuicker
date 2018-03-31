# AndroidQuicker
AndroidQuicker is a powerful & easy to use common library for Android

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
compile 'com.github.carson2440:AndroidQuicker:1.0.2'
```
**STEP 2**

Add the code where you want to use,such as the application class.
``` java
public class BaseApplication extends Application{
        ...
          @Override
            protected void onCreate() {
                ...
                QAppHandler.with(this).create();
                QAndroid.enableStrictMode(this);
                QLogger.init(BuildConfig.DEBUG);

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
                 QLogger.debug("onSubscribe");
             }

             @Override
             public void onNext(NewsList newsList) {
                 initData(new Gson().toJson(newsList));
                 QLogger.debug("onNext" + new Gson().toJson(newsList));
             }

             @Override
             public void onError(Throwable e) {
                 QLogger.debug("Throwable" + e);
                 initData("error:" + e.getMessage());
             }

             @Override
             public void onComplete() {
                 QLogger.debug("onComplete");
             }
         });
 ```

**That is all done!**

