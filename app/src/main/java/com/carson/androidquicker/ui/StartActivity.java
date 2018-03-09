package com.carson.androidquicker.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.carson.androidquicker.QuickerActivity;
import com.carson.androidquicker.R;

/**
 * Created by carson on 2018/3/9.
 */

public class StartActivity extends QuickerActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
