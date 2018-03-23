package com.carson.androidquicker.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carson.androidquicker.R;
import com.carson.androidquicker.databinding.FragmentNetworkBinding;

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
        return binding.getRoot();
    }





}
