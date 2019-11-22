package com.carson.quicker.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by carson on 2018/3/9.
 * 实践证明 Luban的压缩是原分辨率无损压缩，可用QBitmaps先缩放图片再用luban压缩，这样得到的图片存储大小最小
 * 降低图片采样率或者尺寸缩放压缩的图片都会存在锯齿
 */

public class QBitmaps {

    /**
     * bitmap 转换成base64 字符串
     *
     * @param bitmap
     * @param quality 1-100
     * @param isAlpha true=png false = jpeg
     * @return
     */
    public static String toBase64(Bitmap bitmap, int quality, boolean isAlpha) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (isAlpha) {
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }
        byte[] images = baos.toByteArray();
        return Base64.encode(images);
    }

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
     * 将图片以 JPEG 格式输出到文件
     *
     * @param bitmap
     * @param target
     */
    public static boolean saveToFile(Bitmap bitmap, Bitmap.CompressFormat format, File target) {
        try {
            QFileUtil.forceDelete(target);
            FileOutputStream out = new FileOutputStream(target);
            bitmap.compress(format, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 将图片生成圆角图片
     *
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return output;
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

    /**
     * 图片等比例缩放
     *
     * @param origin
     * @param reqWidthAndHeight 缩放到dest参考值-宽高
     * @param maxWidthAndHeight 缩放后宽高是否都不超过参考值，宽高都没有参考值大就不做处理
     * @return
     */
    public static Bitmap compressBitmap(Bitmap origin, int reqWidthAndHeight, boolean maxWidthAndHeight) {
        if (QStrings.isNotEmpty(origin)) {
            int width = origin.getWidth();
            int height = origin.getHeight();

            // 计算宽高缩放率
            float scaleWidth = ((float) reqWidthAndHeight) / width;
            float scaleHeight = ((float) reqWidthAndHeight) / height;
            float scale;
            if (maxWidthAndHeight) {
                scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth;//取小
            } else {
                scale = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;//取大
            }
            if (scale < 1) {
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);
                Bitmap bitmap = Bitmap.createBitmap(origin, 0, 0, width,
                        height, matrix, true);
                if (!origin.isRecycled()) {
                    origin.recycle();
                }
                return bitmap;
            }
        }
        return origin;
    }

    /**
     * 压缩图片<br/>
     * 通过设定压缩后的宽高的最大像素，将图片等比例缩小<br/>
     * 先通过降低取样点，将图片压缩到比目标宽高稍大一点，然后再通过Matrix将图片精确调整到目标大小<br/>
     * 压缩后图像使用RGB_565模式，即每个像素占位2字节，限定大小压缩<br/>
     * 若被压缩图片本身就小于限定大小，则不改变其大小，只更改图像颜色模式为RGB_565<br/>
     * 由于inSampleSize压缩比这个参数在不同手机表现不同，有的手机可以取任意整数，有的手机只能取2的幂数，则通过混合压缩的方式保证压缩的结果一致<br/>
     *
     * @param imgPath   原图片路径
     * @param reqWidth  最大宽度
     * @param reqHeight 最大高度
     * @return 压缩后的bitmap
     */
    public static Bitmap compressBitmap(String imgPath, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 读取大小
        options.inPreferredConfig = Bitmap.Config.RGB_565;// 设置图片每个像素占2字节，没有透明度
        BitmapFactory.decodeFile(imgPath, options);// options读取图片

        double outWidth = options.outWidth;
        double outHeight = options.outHeight;// 获取到当前图片宽高
        int inSampleSize = 1;

        /*
        先计算原图片宽高比ratio=width/height，再计算限定的范围的宽高比比reqRatio，
        若reqRatio > ratio，则说明限定的范围更加细长，则以高为标准计算inSampleSize
        否则，则说明限定范围更加粗矮，则以宽为计算标准
         */
        double ratio = outWidth / outHeight;
        double reqRatio = reqWidth / reqHeight;
        if (reqRatio > ratio)
            while (outHeight / inSampleSize > reqHeight) inSampleSize *= 2;
        else
            while (outWidth / inSampleSize > reqWidth) inSampleSize *= 2;

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        if (1 == inSampleSize) {
            return BitmapFactory.decodeFile(imgPath, options);
        }
        /*
        否则的话，先将图片通过减少采样点的方式，以一个比限定范围稍大的尺寸读入内存，
        防止因为图片太大而OOM，以及太大的图片加载时间过长
        然后继续进行压缩的步骤
        */
        options.inSampleSize = inSampleSize / 2;
        Bitmap baseBitmap = BitmapFactory.decodeFile(imgPath, options);

        /*
        使用之前计算过的宽高比，
        若reqRatio > ratio，则说明限定的范围更加细长，则以高为标准计算压缩比
        否则，则说明限定范围更加粗矮，则以宽为计算标准
        */
        float compressRatio = 1;
        if (reqRatio > ratio)
            compressRatio = reqHeight * 1.0f / baseBitmap.getHeight();
        else
            compressRatio = reqWidth * 1.0f / baseBitmap.getWidth();

        Bitmap afterBitmap = Bitmap.createBitmap(
                (int) (baseBitmap.getWidth() * compressRatio),
                (int) (baseBitmap.getHeight() * compressRatio),
                baseBitmap.getConfig());
        Canvas canvas = new Canvas(afterBitmap);
        // 初始化Matrix对象
        Matrix matrix = new Matrix();
        // 根据传入的参数设置缩放比例
        matrix.setScale(compressRatio, compressRatio);
        Paint paint = new Paint();
        // 消除锯齿
        paint.setAntiAlias(true);
        // 根据缩放比例，把图片draw到Canvas上
        canvas.drawBitmap(baseBitmap, matrix, paint);
        return afterBitmap;
    }

    /**
     * 等比例缩小图片到文件大小maxSize KB<br/>
     *
     * @param imgPath 原图片路径
     * @param maxSize 压缩后文件大小，单位为kb
     * @return 压缩后的bitmap
     */
    public static Bitmap compressBitmap(String imgPath, int maxSize) {
        long area = maxSize * 1024;// 每个像素占2字节，将需求大小转为像素面积

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 读取大小不读取内容
        options.inPreferredConfig = Bitmap.Config.RGB_565;// 设置图片每个像素占2字节，没有透明度
        BitmapFactory.decodeFile(imgPath, options);// options读取图片

        double outWidth = options.outWidth;
        double outHeight = options.outHeight;// 获取到当前图片宽高

        int inSampleSize = 1;
        while ((outHeight / inSampleSize) * (outWidth / inSampleSize) > area)
            inSampleSize *= 2;

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        if (1 == inSampleSize) {
            // inSampleSize == 1，就说明原图比要求的尺寸小或者相等，那么不用继续压缩，直接返回。
            return BitmapFactory.decodeFile(imgPath, options);
        }

        /*
        否则的话，先将图片通过减少采样点的方式，以一个比限定范围稍大的尺寸读入内存，
        防止因为图片太大而OOM，以及太大的图片加载时间过长
        然后继续进行压缩的步骤
        */
        options.inSampleSize = inSampleSize / 2;
        Bitmap baseBitmap = BitmapFactory.decodeFile(imgPath, options);

        /*
        目标大小的面积与现在图片大小的面积的比的平方根，就是缩放比
        java Math.sqrt() 函数不能开小数，而且先计算除法，再计算开放，再对结果求反误差很大，所以做两次开方计算
         */
        float compressRatio = 1;
        compressRatio = (float) (Math.sqrt(area) / Math.sqrt(baseBitmap.getWidth() * baseBitmap.getHeight()));

        Bitmap afterBitmap = Bitmap.createBitmap(
                (int) (baseBitmap.getWidth() * compressRatio),
                (int) (baseBitmap.getHeight() * compressRatio),
                baseBitmap.getConfig());
        Canvas canvas = new Canvas(afterBitmap);
        // 初始化Matrix对象
        Matrix matrix = new Matrix();
        // 根据传入的参数设置缩放比例
        matrix.setScale(compressRatio, compressRatio);
        Paint paint = new Paint();
        // 消除锯齿
        paint.setAntiAlias(true);
        // 根据缩放比例，把图片draw到Canvas上
        canvas.drawBitmap(baseBitmap, matrix, paint);
        return afterBitmap;
    }

}
