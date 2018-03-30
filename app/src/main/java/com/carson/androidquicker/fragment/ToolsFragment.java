package com.carson.androidquicker.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.carson.androidquicker.R;
import com.carson.androidquicker.databinding.FragmentToolsBinding;
import com.carson.quicker.view.scroller.PullUpDownLayout;

/**
 * Created by carson on 2018/3/20.
 */

public class  ToolsFragment extends Fragment {
    FragmentToolsBinding binding;
    PullUpDownLayout pullUpDownLayout;
    TextView message;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tools, container, false);
        View parent = binding.getRoot();
        pullUpDownLayout = parent.findViewById(R.id.pullload);
        message = binding.message;
        pullUpDownLayout.setOnLoadListener(new PullUpDownLayout.OnLoadListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(), "onRefresh start", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullUpDownLayout.onRefreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore() {

            }
        });
        return parent;
    }
}
