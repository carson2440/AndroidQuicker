package com.carson.androidquicker.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carson.androidquicker.QuickerApplication;
import com.carson.androidquicker.R;
import com.carson.androidquicker.adapter.NetWorkAdapter;
import com.carson.androidquicker.bean.NewsList;
import com.carson.androidquicker.databinding.FragmentNetworkBinding;
import com.carson.quicker.Log.QLogger;
import com.carson.quicker.QFragment;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by carson on 2018/3/20.
 */

public class NetWorkFragment extends QFragment {

    FragmentNetworkBinding binding;
    NetWorkAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new NetWorkAdapter(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_network, container, false);
//        binding.loading.setOnClickListener(view -> startActivity(new Intent(this.getActivity(), SDCardReadOrWriteActivity.class)));
        binding.listView.setAdapter(adapter);

        QuickerApplication.dataSource.getLatestNews().delay(2, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).compose(bindToLifecycle()).subscribe(new Observer<NewsList>() {
            @Override
            public void onSubscribe(Disposable d) {
                binding.setIsLoading(true);
                QLogger.debug("onSubscribe");
            }

            @Override
            public void onNext(NewsList list) {
                binding.setIsLoading(false);
                adapter.setBindData(list.getStories(), true);
            }

            @Override
            public void onError(Throwable e) {
                binding.setIsLoading(true);
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
