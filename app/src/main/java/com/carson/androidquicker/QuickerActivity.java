package com.carson.androidquicker;

import android.content.Intent;

import com.carson.quicker.QActivity;

/**
 * Created by carson on 2018/3/9.
 */

public abstract class QuickerActivity extends QActivity{


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void startActivity(Class<?> classz, boolean closeSelf) {
        startActivity(new Intent(this, classz));
        if (closeSelf) {
            finish();
        }
    }


}
