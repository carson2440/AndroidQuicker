package com.carson.quicker.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by carson on 2018/3/9.
 */

public class QHttpSocket {

    //socket connection timeout
    private final long TIMEOUT_SO = 10;
    //socket read or wirte timeout
    private final long TIMEOUT_IO = 8;

    private String httpBaseUrl = "http://news-at.zhihu.com/api/4/";
    private OkHttpClient okHttpClient;
    OkHttpClient.Builder builder;
    Cache httpCache;


    private static class HttpSocketBuilder {
        private static final QHttpSocket INSTANCE = new QHttpSocket();
    }

    private QHttpSocket() {
    }

    public static QHttpSocket load(String httpBaseUrl) {
        HttpSocketBuilder.INSTANCE.httpBaseUrl = httpBaseUrl;
        return HttpSocketBuilder.INSTANCE;
    }

    public QHttpSocket with(OkHttpClient.Builder builder) {
        this.builder = builder;
        return this;
    }

    public QHttpSocket cache(Cache cache) {
        this.httpCache = cache;
        return this;
    }

    OkHttpClient.Builder getHttpBuilder(boolean debugMode) {
        if (this.builder == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    android.util.Log.d("HttpBuilder", message);
                }
            });
            if (debugMode) {
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            } else {
                interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            }
            this.builder = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(TIMEOUT_SO, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_IO, TimeUnit.SECONDS);
        }
        if (this.httpCache != null) {
            this.builder.cache(this.httpCache);
        }
//        builder.authenticator(new DigestAuthenticator(null, "username", "password"));
        return this.builder;
    }

    public <T> T create(Class<T> tClass, boolean debugMode) {
        if (okHttpClient == null) {
            synchronized (QHttpSocket.class) {
                if (okHttpClient == null) {
                    okHttpClient = getHttpBuilder(debugMode).build();
                }
            }
        }
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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(httpBaseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(tClass);
    }
}
