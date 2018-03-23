package com.carson.androidquicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by carson on 2018/3/9.
 */

public class QuickerActivity extends AppCompatActivity {

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
