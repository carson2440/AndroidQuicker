package com.carson.quicker.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class QAndroid {

    /*Need to override onRequestPermissionsResult() method in activity*/
    public static boolean hasPermission(@NonNull Activity context, @NonNull String permission, int REQUEST_ID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                //第一请求权限被取消显示的判断，一般可以不写
//                if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
//                    Toast.makeText(context, "需要开启权限:" + permission, Toast.LENGTH_LONG).show();
//                } else {
                //2、申请权限: 参数二：权限的数组；参数三：请求码
                ActivityCompat.requestPermissions(context, new String[]{permission}, REQUEST_ID);
//                }
                return false;
            }
        }
        return true;
    }

    public static boolean hasSDCard() {
        return android.os.Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取APP的缓存目录，优先返回外部缓存目录
     * auto delete file when System's memory not enough.
     * 卸载应用时会删除目录内创建的所有文件。
     *
     * @param context
     * @param dir
     * @return
     */
    public static File getCachedir(Context context, String dir) {
        File cacheDir = null;
        if (hasSDCard()) {
            cacheDir = context.getApplicationContext().getExternalCacheDir();
            if (cacheDir == null) {
                String path = "/Android/data/" + context.getApplicationContext().getPackageName() + "/cache/";
                cacheDir = new File(Environment.getExternalStorageDirectory().getPath() + path);
            }
        }
        if (cacheDir == null) {
            cacheDir = context.getApplicationContext().getCacheDir();
        }
        if (cacheDir == null) {
            cacheDir = new File("/data/data/" + context.getApplicationContext().getPackageName() + "/");
        }
        if (QStrings.isNotEmpty(dir)) {
            cacheDir = new File(cacheDir, dir);
        }
        if (!cacheDir.exists()) {
            QFileUtil.mkdirs(cacheDir);
        }
        return cacheDir;
    }

    /**
     * Call this method need to checkSelfPermission of Manifest.permission.WRITE_EXTERNAL_STORAGE
     * such as : if(Storages.hasPermission(Activity,Manifest.permission.WRITE_EXTERNAL_STORAGE,1)){
     * <p>
     * };
     *
     * @since android 6.0,Context.getExternalFilesDir( type ) is recommend
     */
    public static File getSDCard(String dir) {
        File sdcard = null;
        if (hasSDCard()) {
            sdcard = new File(getSDCardPath(), dir);
            if (!sdcard.exists()) {
                QFileUtil.mkdirs(sdcard);
            }
        }
        return sdcard;
    }

    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }


    /**
     * 判断当前运行环境是否是debug模式
     *
     * @param context
     * @return
     */
    public static boolean isDebug(Context context) {
        if (context == null) {
            return false;
        }
        ApplicationInfo appInfo = context.getApplicationInfo();
        return (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    @SuppressLint("NewApi")
    public static void enableStrictMode(Context context) {
        if (isDebug(context)) {
            /* 严苛模式*/
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder().detectAll();
            if (Build.VERSION.SDK_INT >= 11) {
                threadPolicyBuilder = threadPolicyBuilder.penaltyLog().penaltyDialog().penaltyFlashScreen();
                // vmPolicyBuilder.setClassInstanceLimit(ImageGridActivity.class,
                // 1).setClassInstanceLimit(ImageDetailActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());

            StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder().detectAll();
//            StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects();
//
//            int targetSdk = Build.VERSION.SDK_INT;
//            if (targetSdk >= Build.VERSION_CODES.JELLY_BEAN) {
//                vmPolicyBuilder = vmPolicyBuilder.detectLeakedRegistrationObjects();
//            }
//            if (targetSdk >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                vmPolicyBuilder = vmPolicyBuilder.detectFileUriExposure();
//            }
//            if (targetSdk >= Build.VERSION_CODES.O) {
//                vmPolicyBuilder = vmPolicyBuilder.detectUntaggedSockets().detectContentUriWithoutPermission();
//            }
            //例如使用penaltyDeath()的话，一旦StrictMode消息被写到LogCat后应用就会崩溃
            StrictMode.setVmPolicy(vmPolicyBuilder.penaltyLog().build());
        }

    }

    /**
     * get Manifest Meta Data
     *
     * @param context
     * @param metaKey
     * @return
     */
    public static String getMetaData(Context context, String metaKey) {
        String name = context.getPackageName();
        ApplicationInfo appInfo;
        String msg = "";
        try {
            appInfo = context.getPackageManager().getApplicationInfo(name, PackageManager.GET_META_DATA);
            msg = appInfo.metaData.getString(metaKey);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

    /**
     * uninstall apk
     *
     * @param context
     * @param packageName
     */
    public static void uninstall(Context context, String packageName) {
        // 通过程序的包名创建URI
        Uri packageURI = Uri.parse("package:" + packageName);
        // 创建Intent意图
        Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
        // 执行卸载程序
        context.startActivity(intent);
    }

    //建议将dexCrc值放在服务器做校验,用crc32对classes.dex文件的完整性进行校验
    public static String getDexCrc32(Context context) {
        try {
            ZipFile zipfile = new ZipFile(context.getPackageCodePath());
            ZipEntry dexentry = zipfile.getEntry("classes.dex");
            zipfile.close();
            return Long.toHexString(dexentry.getCrc());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取APP当前的版本号
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String version = "";
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获取APP当前的构建号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 检测APP是否开启通知显示，作用于APP内。
     */
    public static boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return true;
        } else {
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;

            Class appOpsClass = null;
            /* Context.APP_OPS_MANAGER */
            try {
                appOpsClass = Class.forName(AppOpsManager.class.getName());
                Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE,
                        String.class);
                Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");

                int value = (Integer) opPostNotificationValue.get(Integer.class);
                return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
