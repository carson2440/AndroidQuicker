package com.carson.androidquicker.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carson.androidquicker.BR;
import com.carson.androidquicker.R;
import com.carson.androidquicker.bean.News;
import com.carson.androidquicker.databinding.ItemNewsBinding;
import com.carson.quicker.adapter.QAdapter;

public class NetWorkAdapter extends QAdapter<News> {

    private ItemNewsBinding binding;


    public NetWorkAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_news, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemNewsBinding) convertView.getTag();
        }
        binding.setVariable(BR.news, getItem(position));
        return convertView;
    }
}
