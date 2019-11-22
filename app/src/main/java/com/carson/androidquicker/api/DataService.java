package com.carson.androidquicker.api;

import com.carson.androidquicker.bean.NewsList;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by carson on 2018/3/9.
 */

public interface DataService {
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
