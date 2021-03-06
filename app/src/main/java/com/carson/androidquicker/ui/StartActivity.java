package com.carson.androidquicker.ui;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.carson.androidquicker.QuickerActivity;
import com.carson.androidquicker.R;
import com.carson.androidquicker.databinding.ActivityStartBinding;
import com.carson.androidquicker.vo.StartMode;
import com.carson.quicker.QExecutors;
import com.carson.quicker.codec.QAESCoder;
import com.carson.quicker.logger.QLogger;
import com.carson.quicker.utils.QAndroid;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by carson on 2018/3/9.
 */

public class StartActivity extends QuickerActivity {
    ActivityStartBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*问题描述：应用安装后，通过安装界面的打开按钮打开应用，每当应用从后台切换到前台，都会启动欢迎界面。如果是通过点击应用启动，则没有此问题。*/
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        QExecutors.with().threadIO().execute(() -> QLogger.d("CRC32:" + QAndroid.getDexCrc32(StartActivity.this)));

        binding = DataBindingUtil.setContentView(this, R.layout.activity_start);
        StartMode viewMode = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(StartMode.class);
        viewMode.message.set("Hi,welcome to AndroidQuicker!" + QAndroid.isNotificationEnabled(this));
        /*dataBinding 不能绑定空对象,否则不能同步数据,可以多次绑定数据，可以直接绑定一个ViewModel*/
        binding.setStartMode(viewMode);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(() -> {
                    QLogger.d("Unsubscribing subscription from onStart()");
                }).compose(bindToLifecycle()).subscribe(aLong -> {
            QLogger.d("Started in onStart(), running until: " + aLong);
        });

        loadAction();

    }

    private void loadAction() {
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            for (int i = 3; i >= 0; i--) {
                emitter.onNext(i);
                Thread.currentThread().sleep(800);
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.computation())
                .doOnDispose(() -> QLogger.d("doOnDispose  for loadAction"))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        binding.getStartMode().skip.set("跳过(" + integer + ")");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        startActivity(HomeActivity.class, true);
                    }
                });
    }

    public void messageClick(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QLogger.d("call ondestroy().");
    }
}
