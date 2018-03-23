package com.carson.androidquicker.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.carson.androidquicker.R;
import com.carson.quicker.view.QZoomImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carson on 2018/3/20.
 */

public class LoopPagerAdapter extends PagerAdapter {
    Context context;
    List<String> datas = new ArrayList<>();

    public LoopPagerAdapter(Context context) {
        this.context = context;
        datas.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1515556871&di=997a8271f26d3a23ca4cd30e4675b608&imgtype=jpg&er=1&src=http%3A%2F%2Fdown1.cnmo.com%2Fcnmo-app%2Fa208%2Fmayiigaoqing.jpg");
        datas.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514962251448&di=3800fd419a96900ffa3db16dcc156def&imgtype=0&src=http%3A%2F%2Fimg.zybus.com%2Fuploads%2Fallimg%2F131213%2F1-131213111R2-50.jpg");
        datas.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514962150832&di=85a70f6450d46327895797684612ef12&imgtype=0&src=http%3A%2F%2Fdown1.cnmo.com%2Fcnmo-app%2Fa198%2Fhuojianhua.jpg");
        datas.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514962274347&di=8657b5c549bdf5e374f71d63e3bfee06&imgtype=0&src=http%3A%2F%2Fimg5.niutuku.com%2Fphone%2F1301%2F4055%2F4055-niutuku.com-365704.jpg");
        datas.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514962388636&di=14aecab743849c7a9d4c050477cb8345&imgtype=0&src=http%3A%2F%2Fimg4.duitang.com%2Fuploads%2Fitem%2F201409%2F19%2F20140919161118_8w3xk.jpeg");
        datas.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514968127434&di=53a3b2c7ee1b7ecf91edd91ed81ecfb5&imgtype=0&src=http%3A%2F%2Fpic.ffpic.com%2Ffiles%2F2015%2F0309%2F0308zlyxcxzazbz6.jpg");
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        String url = datas.get(position);
        QZoomImageView imageView = new QZoomImageView(this.context);
        imageView.setImageResource(R.drawable.ic_loading);

        Glide.with(this.context).load(url).placeholder(R.mipmap.ic_launcher_round).into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
