package com.carson.androidquicker.ui;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.carson.androidquicker.QuickerActivity;
import com.carson.androidquicker.R;
import com.carson.androidquicker.fragment.HomeFragment;
import com.carson.androidquicker.fragment.NetWorkFragment;
import com.carson.androidquicker.fragment.TestFragment;
import com.carson.androidquicker.fragment.ToolsFragment;
import com.carson.quicker.logger.QLogger;
import com.carson.quicker.utils.QAndroid;
import com.carson.quicker.utils.QBitmaps;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by carson on 2018/3/21.
 */

public class HomeActivity extends QuickerActivity implements BottomNavigationBar.OnTabSelectedListener {

    BottomNavigationBar navigationBar;
    int lastSelectedPosition = 0;
    HomeFragment homeFragment;
    NetWorkFragment netWorkFragment;
    ToolsFragment toolsFragment;
    TestFragment testFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        QLogger.v("tag", "message test %s", "level v");
        QLogger.verbose("message test level verbose");
        QLogger.d("tag", "message test %s", "level d");
        QLogger.debug("message test level debug");
        QLogger.i("tag", "message test %s", "level i");
        QLogger.info("message test level info");
        QLogger.w("tag", "message test %s", "level w");
        QLogger.warn("message test level warn");

        QLogger.e("tag", "message test %s", "level e");
        QLogger.error("message test level error");

        QLogger.log(Log.ASSERT, "carson", "this is just message");

        QLogger.d(getLocalClassName() + "Task ID:" + getTaskId() + " Hash code:" + this.hashCode());
        initBottomBarView(
                0);
    }

    private Bitmap loadAppBitmap() {
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("image.png");
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap != null) {
                bitmap = QBitmaps.compressBitmap(bitmap, 200, true);
                bitmap = QBitmaps.toRoundCorner(bitmap, QAndroid.dp2px(this, 20));

                File file = new File(QAndroid.getCachedir(this, "image"), "image.png");
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

    private boolean isNotificationEnabled(Context context) {
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


    private void showAppDetailIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(localIntent);
    }

    private void initBottomBarView(int defaultSelectedposition) {
        navigationBar = findViewById(R.id.bottom_navigation_bar);

        navigationBar.setAutoHideEnabled(true);
        //BottomNavigationBar.MODE_SHIFTING;
        //BottomNavigationBar.MODE_FIXED;
        //BottomNavigationBar.MODE_DEFAULT;
        navigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        // BottomNavigationBar.BACKGROUND_STYLE_DEFAULT;
        // BottomNavigationBar.BACKGROUND_STYLE_RIPPLE
        // BottomNavigationBar.BACKGROUND_STYLE_STATIC
        navigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        navigationBar.setBarBackgroundColor(R.color.black_floating);//背景颜色
        navigationBar.setInActiveColor(R.color.font_white);//未选中时的颜色
        navigationBar.setActiveColor(R.color.button_select);//选中时的颜色

        TextBadgeItem badgeItem = new TextBadgeItem();
        badgeItem.setText("99").setHideOnSelect(true);
        navigationBar.addItem(new BottomNavigationItem(R.drawable.ic_home_24, "Home"))
                .addItem(new BottomNavigationItem(R.drawable.ic_network_24, "NetWork").setBadgeItem(badgeItem))
                .addItem(new BottomNavigationItem(R.drawable.ic_tools_24, "Tools"))
                .addItem(new BottomNavigationItem(R.drawable.ic_tools_24, "Test"))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise();

        navigationBar.setTabSelectedListener(this);
        onTabSelected(defaultSelectedposition);
    }


    @Override
    public void onTabSelected(int position) {
        lastSelectedPosition = position;
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (position) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                }
                transaction.replace(R.id.fragment_container, homeFragment);
//                getSupportActionBar().setTitle("Home");
                break;
            case 1:
                if (netWorkFragment == null) {
                    netWorkFragment = new NetWorkFragment();
                }
                transaction.replace(R.id.fragment_container, netWorkFragment);
//                getSupportActionBar().setTitle("Network");
                break;
            case 2:
                if (toolsFragment == null) {
                    toolsFragment = new ToolsFragment();
                }
                transaction.replace(R.id.fragment_container, toolsFragment);
//                getSupportActionBar().setTitle("Tools");
                break;
            case 3:
                if (testFragment == null) {
                    testFragment = new TestFragment();
                }
                transaction.replace(R.id.fragment_container, testFragment);
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    MenuItem moreItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        moreItem = menu.add(Menu.NONE, Menu.FIRST, Menu.FIRST, "白天/夜晚");
        moreItem.setIcon(R.drawable.ic_network_24);
        moreItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        moreItem = menu.add(Menu.NONE, Menu.FIRST + 1, Menu.FIRST + 1, "检测通知权限");
        moreItem.setIcon(R.drawable.ic_tools_24);
        moreItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        moreItem = menu.add(Menu.NONE, Menu.FIRST + 2, Menu.FIRST + 2, "关于APP");
        moreItem.setIcon(R.drawable.ic_home_24);
        moreItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Menu.FIRST:
                int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
//                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
//                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                // 同样需要调用recreate方法使之生效
                recreate();
                break;
            case Menu.FIRST + 1:
                boolean allow = isNotificationEnabled(this);
                if (allow) {
                    Toast.makeText(this, "已经允许APP显示通知信息", Toast.LENGTH_LONG).show();
                } else {
                    showAppDetailIntent();
                }
                break;
            case Menu.FIRST + 2:
                startActivity(ActAboutApp.class, false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //    /** Shows the product detail fragment */
//    public void show(Product product) {
//
//        ProductFragment productFragment = ProductFragment.forProduct(product.getId());
//
//        getSupportFragmentManager()
//                .beginTransaction()
//                .addToBackStack("product")
//                .replace(R.id.fragment_container,
//                        productFragment, null).commit();
//    }
}
