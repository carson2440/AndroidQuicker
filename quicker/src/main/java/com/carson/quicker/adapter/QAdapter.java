package com.carson.quicker.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.carson.quicker.utils.QStrings;

import java.util.ArrayList;
import java.util.List;

public abstract class QAdapter<E> extends BaseAdapter {
    private List<E> listData = new ArrayList<>();
    private Context context;

    public QAdapter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }

    public List<E> getBindData() {
        return this.listData;
    }

    public int setBindData(List<E> list, boolean clearAll) {
        if (clearAll) {
            this.listData.clear();
        }
        if (QStrings.isNotEmpty(list)) {
            listData.addAll(list);
        }
        notifyDataSetChanged();
        return getCount();
    }

    @Override
    public int getCount() {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public E getItem(int position) {
        return QStrings.isNotEmpty(listData) ? listData.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);


}
