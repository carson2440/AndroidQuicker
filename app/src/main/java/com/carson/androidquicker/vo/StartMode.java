package com.carson.androidquicker.vo;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.graphics.Bitmap;

/**
 * Created by carson on 2018/3/9.
 */

public class StartMode extends ViewModel {
    public final ObservableField<String> message = new ObservableField<>();
    public final ObservableField<String> skip = new ObservableField<>();

}
