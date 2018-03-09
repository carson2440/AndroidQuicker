package com.carson.quicker.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by carson on 2018/3/9.
 */

public class QBitmaps {

    /**
     * convert Bitmap to byte array
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
        return out.toByteArray();
    }

    /**
     * 把bitmap转换成Base64编码String
     *
     * @param bitmap
     * @return String
     */
    public static String bitmapToStr(Bitmap bitmap) {
        return Base64.encodeToString(bitmapToByte(bitmap), Base64.DEFAULT);
    }

    /**
     * 圆角bitmap
     *
     * @param bitmap
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawCircle(width / 2, height / 2, width / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 保存Bitmap到文件
     *
     * @param bitmap
     * @param target
     */
    public static void saveBitmap(Bitmap bitmap, File target) {
        saveBitmap(bitmap, 100, target);
    }

    /**
     * 保存Bitmap到文件
     *
     * @param bitmap
     * @param quality 保存质量 0..100
     * @param target
     */
    public static void saveBitmap(Bitmap bitmap, int quality, File target) {
        if (target.exists()) {
            target.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(target);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getBitmapByteSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Workaround for KitKat initial release NPE in Bitmap, fixed in
            // MR1. See issue #148.
            try {
                return bitmap.getAllocationByteCount();
            } catch (NullPointerException e) {
                // Do nothing.
            }
        }
        return bitmap.getHeight() * bitmap.getRowBytes();
    }

    public static Bitmap getScreenShot(Activity activity) {
        if (activity == null) {
            return null;
        }
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();

        return b;
    }

}
