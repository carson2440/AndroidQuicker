package com.carson.quicker.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.Formatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by carson on 2018/3/9.
 * 文件和权限相关操作
 */

public class QStorages { /* Checks if external storage is available for read and write */
    public static boolean hasSDCard() {
        return android.os.Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

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

    /*Need to override onRequestPermissionsResult() method in activity*/
    public static boolean hasSDCardAndPermission(@NonNull Activity activity, int WRITE_EXTERNAL_STORAGE_ID) {
        return hasSDCard() && hasPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_ID);
    }

    /**
     * Call this method need to checkSelfPermission of Manifest.permission.WRITE_EXTERNAL_STORAGE
     * such as : if(Storages.hasPermission(Activity,Manifest.permission.WRITE_EXTERNAL_STORAGE,1)){
     * <p>
     * };
     *
     * @since android 6.0,Context.getExternalFilesDir( type ) is recommend
     */
    public static File getSDCard(@Nullable String child) {
        File sdcard = null;
        if (hasSDCard()) {
            sdcard = new File(Environment.getExternalStorageDirectory(), child);
            if (!sdcard.exists()) {
                mkdirs(sdcard);
            }
        }
        return sdcard;
    }


    /**
     * 调用 getExternalFilesDir() 并传递 null。返回外部存储应用的专用目录的根目录。
     * 切记 getExternalFilesDir() 卸载应用时会删除目录内创建的所有文件。
     * <p>
     * 如果您希望卸载应用后仍然可以保留文件。
     * 比如，照相机拍照要保留照片时—应改用 getSDCard()或者getExternalStoragePublicDirectory()。
     */
    public static File getExternalFilesDir(Context context, String dir) {
        return new File(context.getApplicationContext().getExternalFilesDir(null), dir);
    }

    /**
     * auto delete file when System's memory not enough.
     * 卸载应用时会删除目录内创建的所有文件。
     */
    public static File getCacheDir(Context context, String dir) {
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
        if (!TextUtils.isEmpty(dir)) {
            cacheDir = new File(cacheDir, dir);
        }
        if (!cacheDir.exists()) {
            mkdirs(cacheDir);
        }
        return cacheDir;
    }

    /**
     * Formats a content size to be in the form of bytes, kilobytes, megabytes,
     * etc. {@link Formatter#formatFileSize Formatter.formatFileSize(context, sizeBytes)}
     *
     * @param context   Context to use to load the localized units
     * @param sizeBytes size value to be formatted, in bytes
     * @return formatted string with the number
     */
    public static String fileDisplaySize(@Nullable Context context, long sizeBytes) {
        return Formatter.formatFileSize(context.getApplicationContext(), sizeBytes);
    }

    /**
     * Deletes a file. If file is a directory, delete it and all
     * sub-directories.
     * <p/>
     * The difference between File.delete() and this method are:
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>You get exceptions when a file or directory cannot be deleted.
     * (java.io.File methods returns a boolean)</li>
     * </ul>
     *
     * @param file file or directory to delete, must not be {@code null}
     * @throws NullPointerException  if the directory is {@code null}
     * @throws FileNotFoundException if the file was not found
     * @throws IOException           in case deletion is unsuccessful
     */
    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    /**
     * Determines whether the specified file is a Symbolic Link rather than an
     * actual file.
     * <p/>
     * Will not return true if there is a Symbolic Link anywhere in the path,
     * only if the specific file is.
     * <p/>
     * <b>Note:</b> the current implementation always returns {@code false} if
     * the system is detected as Windows using
     *
     * @param file the file to check
     * @return true if the file is a Symbolic Link
     * @throws IOException if an IO error occurs while checking the file
     * @since 2.0
     */
    public static boolean isSymlink(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        }
        if (File.separatorChar == '\\') {
            return false;
        }
        File fileInCanonicalDir = null;
        if (file.getParent() == null) {
            fileInCanonicalDir = file;
        } else {
            File canonicalDir = file.getParentFile().getCanonicalFile();
            fileInCanonicalDir = new File(canonicalDir, file.getName());
        }

        if (fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Deletes a directory recursively.
     *
     * @param directory directory to delete
     * @throws IOException in case deletion is unsuccessful
     */
    public static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        if (!isSymlink(directory)) {
            cleanDirectory(directory);
        }

        if (!directory.delete()) {
            String message = "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        }
    }

    /**
     * Cleans a directory without deleting self.
     *
     * @param directory directory to clean
     * @throws IOException in case cleaning is unsuccessful
     */
    public static void cleanDirectory(File directory) throws IOException {
        if (directory == null || !directory.exists()) {
            return;
        }
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                IOException exception = null;
                for (File file : files) {
                    try {
                        forceDelete(file);
                    } catch (IOException ioe) {
                        exception = ioe;
                    }
                }
                if (null != exception) {
                    throw exception;
                }
            }
        }
    }

    static void forceMkdir(File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                String message = "File " + directory + " exists and is "
                        + "not a directory. Unable to create directory.";
                throw new IOException(message);
            }
        } else {
            if (!directory.mkdirs()) {
                // Double-check that some other thread or process hasn't made
                // the directory in the background
                if (!directory.isDirectory()) {
                    String message = "Unable to create directory " + directory;
                    throw new IOException(message);
                }
            }
        }
    }

    public static boolean mkdirs(File directory) {
        if (null != directory) {
            try {
                if (directory.isFile()) {
                    forceMkdir(directory.getParentFile());
                } else {
                    forceMkdir(directory);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
