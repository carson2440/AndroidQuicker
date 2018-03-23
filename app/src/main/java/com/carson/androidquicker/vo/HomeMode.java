package com.carson.androidquicker.vo;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.MainThread;

import com.carson.androidquicker.bean.Home;

/**
 * Created by carson on 2018/3/20.
 */

public class HomeMode extends ViewModel {
    private MutableLiveData<Home> homeLiveData = new MutableLiveData<>();
    private static HomeMode sInstance;

    public HomeMode() {

    }

    @MainThread
    public static HomeMode get(Context context) {
        if (sInstance == null) {
            sInstance = new HomeMode();
        }
        return sInstance;
    }

    public MutableLiveData<Home> getHomeLiveData() {
        return homeLiveData;
    }

    /**
     * @MainThread
     */
    public void setHome(Home home) {
        if (homeLiveData != null) {
            homeLiveData.setValue(home);
        }
    }
}