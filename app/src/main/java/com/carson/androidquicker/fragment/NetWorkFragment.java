package com.carson.androidquicker.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carson.androidquicker.QuickerApplication;
import com.carson.androidquicker.R;
import com.carson.androidquicker.bean.NewsList;
import com.carson.androidquicker.databinding.FragmentNetworkBinding;
import com.carson.quicker.Log.QLogger;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by carson on 2018/3/20.
 */

public class NetWorkFragment extends Fragment {

    FragmentNetworkBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_network, container, false);
//        binding.loading.setOnClickListener(view -> startActivity(new Intent(this.getActivity(), SDCardReadOrWriteActivity.class)));

        QuickerApplication.dataSource.getLatestNews().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<NewsList>() {
            @Override
            public void onSubscribe(Disposable d) {
                QLogger.debug("onSubscribe");
            }

            @Override
            public void onNext(NewsList newsList) {
                QLogger.debug("onNext" + newsList);
            }

            @Override
            public void onError(Throwable e) {
                QLogger.debug("Throwable" + e);
            }

            @Override
            public void onComplete() {
                QLogger.debug("onComplete");
            }
        });
        return binding.getRoot();
    }


}
