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
        datas.add("https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=480*800%E5%88%86%E8%BE%A8%E7%8E%87&step_word=&hs=0&pn=633&spn=0&di=150499682630&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=2&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=-1&cs=3311316767%2C3418471209&os=4090368693%2C3900171980&simid=3404744251%2C309630727&adpicid=0&lpn=0&ln=1989&fr=&fmq=1521795726255_R&fm=result&ic=0&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fimg.anzow.com%2FSoftware%2Ffiles_images%2F2014109%2F2014100975059721.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bwgz5o_z%26e3Bv54AzdH3F15ogs5w1AzdH3FS5upow6jAzdH3FJRFPEQDMG9_z%26e3Bfip4s&gsm=258&rpstart=0&rpnum=0");
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

