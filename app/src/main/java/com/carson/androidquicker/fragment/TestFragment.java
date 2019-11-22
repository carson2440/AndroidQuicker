package com.carson.androidquicker.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.carson.androidquicker.R;
import com.carson.quicker.QBaseFragment;
import com.carson.quicker.utils.QAndroid;
import com.carson.quicker.utils.QBitmaps;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TestFragment extends QBaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_test, null);
        ImageView imageView = view.findViewById(R.id.test_image);
        imageView.setImageBitmap(loadAppBitmap());
        return view;
    }

    private Bitmap loadAppBitmap() {
        InputStream inputStream = null;
        try {
            inputStream = getActivity().getAssets().open("image.png");
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap != null) {
                bitmap = QBitmaps.compressBitmap(bitmap, QAndroid.dp2px(getContext(), 200), true);
                bitmap = QBitmaps.toRoundCorner(bitmap, QAndroid.dp2px(getContext(), 20));

                File file = new File(QAndroid.getCachedir(getContext().getApplicationContext(), "image"), "image.png");
                QBitmaps.saveToFile(bitmap, Bitmap.CompressFormat.JPEG, file);
                //todo Luban
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
