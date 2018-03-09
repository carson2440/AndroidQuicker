package com.carson.androidquicker.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.carson.androidquicker.QuickerActivity;
import com.carson.androidquicker.R;
import com.carson.androidquicker.databinding.ActivityStartBinding;
import com.carson.androidquicker.vo.StartMode;
import com.carson.quicker.Log.QLogger;
import com.carson.quicker.QExecutors;
import com.carson.quicker.utils.QAndroid;
import com.carson.quicker.utils.QStorages;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by carson on 2018/3/9.
 */

public class StartActivity extends QuickerActivity {
    ActivityStartBinding binding;
    Disposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         /*问题描述：应用安装后，通过安装界面的打开按钮打开应用，每当应用从后台切换到前台，都会启动欢迎界面。如果是通过点击应用启动，则没有此问题。*/
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        boolean result = QStorages.hasSDCardAndPermission(this, 001);
        QLogger.debug("has sdcard permission:" + result);
        QExecutors.builder().threadIO().execute(new Runnable() {
            @Override
            public void run() {
                QLogger.error(new BigInteger(String.valueOf(QAndroid.getDexCrc32(StartActivity.this))).toString(16));
            }
        });

        binding = DataBindingUtil.setContentView(this, R.layout.activity_start);
        StartMode viewMode = ViewModelProviders.of(this).get(StartMode.class);
        viewMode.message.set("Hi,carson");
         /*dataBinding 不能绑定空对象,否则不能同步数据,可以多次绑定数据，可以直接绑定一个ViewModel*/
        binding.setStartMode(viewMode);

        disposable = Observable.interval(1,1, TimeUnit.SECONDS).subscribe(aLong -> viewMode.message.set("ticks:" + aLong));
    }


    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();
    }
}
