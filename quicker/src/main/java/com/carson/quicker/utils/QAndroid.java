package com.carson.quicker.utils;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by carson on 2018/3/9.
 */

public class QAndroid {

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
        return QStrings.EMPTY;
    }

    /**
     * useage:
     * 1.getUnixTime()
     * 2.getUnixTime("MM/dd HH:mm:ss")
     * 3.getUnixTime(String.valueOf(System.currentTimeMillis()),"MM/dd HH:mm:ss")
     */
    public static String getUnixTime(String... text) {
        String formater = "yyyy/MM/dd HH:mm:ss";
        Date date = new Date(System.currentTimeMillis());
        if (text.length > 0) {
            if (QStrings.isDigits(text[0])) {
                date.setTime(Long.parseLong(text[0]));
                if (text.length > 1) {
                    formater = text[0];
                }
            } else {
                formater = text[0];
            }
        }
        SimpleDateFormat format = new java.text.SimpleDateFormat(formater, Locale.CHINA);
        return format.format(date);
    }


    /**
     * android.permission.ACCESS_NETWORK_STATE,(Allows applications to access information about networks)
     */
    public static boolean networkOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null ? ni.isConnected() : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * android.permission.ACCESS_NETWORK_STATE,(Allows applications to access information about networks)
     */
    public static boolean wifiOnLine(Context context) {
        ConnectivityManager manager = (ConnectivityManager) (context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (manager != null) {
            NetworkInfo network = manager.getActiveNetworkInfo();
            if (network != null && network.getType() == ConnectivityManager.TYPE_WIFI) {
                return network.isAvailable() && network.isConnected();
            }
        }
        return false;
    }

    /**
     * 判断当前设备是否为手机
     *
     * @param context
     * @return
     */
    public static boolean isMobile(Context context) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            return false;
        } else {
            return true;
        }
    }

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static String getVersionName(Context context) {
        String version = "";
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

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
