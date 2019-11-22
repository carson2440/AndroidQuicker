package com.carson.quicker.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by carson on 2019/11/20.
 */
public class QHttpSocket {
    private boolean debugMode = false;

    //socket connection timeout
    private final long TIMEOUT_SO = 10;
    //socket read or wirte timeout
    private final long TIMEOUT_IO = 15;

    private OkHttpClient okHttpClient;
    private OkHttpClient.Builder okHttpBuilder;
    private Cache httpCache;
    private Retrofit.Builder retrofitBuilder;


    private static class HttpSocketBuilder {
        private static final QHttpSocket INSTANCE = new QHttpSocket();
    }

    private QHttpSocket() {
    }

    public static QHttpSocket with() {
        return HttpSocketBuilder.INSTANCE;
    }

    public QHttpSocket setDebugMode(boolean debug) {
        this.debugMode = debug;
        return this;
    }

    public QHttpSocket enableCache(Cache cache) {
        this.httpCache = cache;
        return this;
    }


    public OkHttpClient.Builder defaultHttpBuilder() {
        if (this.okHttpBuilder == null) {
            this.okHttpBuilder = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_SO, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_IO, TimeUnit.SECONDS);
        }

        if (this.httpCache != null) {
            this.okHttpBuilder.cache(this.httpCache);
        }
//        builder.authenticator(new DigestAuthenticator(null, "username", "password"));

        if (debugMode) {
            HttpLogInterceptor interceptor = new HttpLogInterceptor(new HttpLogInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.d("QHttpSocket", message);
                }
            });
            interceptor.setLevel(HttpLogInterceptor.Level.BASIC);
            this.okHttpBuilder.addInterceptor(interceptor);
        }
        return this.okHttpBuilder;
    }


    public QHttpSocket setHttpBuilder(OkHttpClient.Builder okHttpBuilder) {
        this.okHttpBuilder = okHttpBuilder;
        return this;
    }

    public Retrofit.Builder getRetrofitBuilder() {

        if (this.retrofitBuilder == null) {
            Gson gson;
            if (debugMode) {
                gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
            } else {
                gson = new Gson();
            }
        /*
         //只有服务器支持缓存控制如etag或cache control在请求头部中，设置cache才有效。不然需要写拦截器加入才能启用缓存控制
         //如果解析属性报错而不影响后面属性的解析需要修改Gson源码, 位置在com.google.gsons.internal.bind.ReflectiveTypeAdapterFactory类下的class Adapter<T>中read方法
                    try {
                            field.read(in, instance);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                            in.skipValue();
                        }
        */
            this.retrofitBuilder = new Retrofit.Builder();
            this.retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
            this.retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gson));
        }
        return this.retrofitBuilder;
    }

    public QHttpSocket setRetrofitBuilder(Retrofit.Builder retrofitBuilder) {
        this.retrofitBuilder = retrofitBuilder;
        return this;
    }

    /**
     * @param baseUrl 需要以'/'结尾（need end with '/'）
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T create(String baseUrl, Class<T> tClass) {
        if (okHttpClient == null) {
            synchronized (QHttpSocket.class) {
                if (okHttpClient == null) {
                    okHttpClient = defaultHttpBuilder().build();
                }
            }
        }
        Retrofit.Builder builder = getRetrofitBuilder();
        builder.baseUrl(baseUrl).client(this.okHttpClient);
        return builder.build().create(tClass);
    }
}

