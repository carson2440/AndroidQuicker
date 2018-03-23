package com.carson.androidquicker.fragment;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carson.androidquicker.R;
import com.carson.androidquicker.adapter.LoopPagerAdapter;
import com.carson.androidquicker.bean.Home;
import com.carson.androidquicker.databinding.FragmentHomeBinding;
import com.carson.androidquicker.vo.HomeMode;
import com.carson.quicker.utils.QStrings;
import com.carson.quicker.view.pager.ZoomOutTransformer;

/**
 * Created by carson on 2018/3/20.
 */

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    HomeMode viewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.setIsLoading(true);

        binding.viewPager.setAdapter(new LoopPagerAdapter(getActivity()));
        binding.viewIndicator.setViewPager(binding.viewPager);
        //offscreentPageLimit 值需要大于总条数,不然viewpager自动滚动到临界值放大缩小不起作用。
        binding.viewPager.setOffscreenPageLimit(7);
        binding.viewPager.setPageTransformer(true, new ZoomOutTransformer());
        ViewGroup.LayoutParams layoutParams = binding.viewPager.getLayoutParams();
        layoutParams.width = ((Activity) binding.viewPager.getContext()).getWindowManager().getDefaultDisplay().getWidth() / 7 * 5;
        layoutParams.height = (int) ((layoutParams.width * 1.6));
        if (binding.viewPager.getParent() instanceof ViewGroup) {
            ViewGroup viewParent = ((ViewGroup) binding.viewPager.getParent());
            viewParent.setClipChildren(false);
            binding.viewPager.setClipChildren(false);
        }
        binding.viewPager.startAutoScroll();
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.message.setText("viewpager pos:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.loading.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Home home = new Home("this is app title.", "mobile number check 19983162569" + QStrings.isPhone("19983162569"));
                viewModel.setHome(home);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        viewModel = HomeMode.get(getContext());
        binding.setHomeViewModel(viewModel);
        subscribeToModel(viewModel);
    }


    private void subscribeToModel(HomeMode model) {
        model.getHomeLiveData().observe(this, new Observer<Home>() {
            @Override
            public void onChanged(@Nullable Home home) {
//                model.getHomeLiveData().setValue(home);
                binding.setIsLoading(false);
                binding.loading.setVisibility(View.GONE);
                binding.message.setText("load data success," + home.getMessage());
                // 18190909598  18180991998

            }
        });
    }
}
