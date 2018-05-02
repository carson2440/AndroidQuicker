package com.carson.androidquicker.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.carson.androidquicker.QuickerActivity;
import com.carson.androidquicker.R;

public class ActAboutApp extends QuickerActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_about_app);
    }
}
