package com.carson.quicker.logger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author yanxu QQ:981385016
 * @name QLogger
 * @class name：com.skyguard.carson.logger
 * @time 2018/10/17 18:36
 * @desc describe
 */
public final class Utils {
    private Utils() {
        // Hidden constructor.
    }

    private static final String SUFFIX_LOCK = ".lck";
    public static final String SUFFIX_ZIP = ".zip";
    private static final int BUFF_SIZE = 1024 * 1024;

    @NonNull
    public static <T> T checkNotNull(@Nullable final T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * Copied from "android.util.Log.getStackTraceString()" in order to avoid usage of Android stack
     * in unit tests.
     *
     * @return Stack trace in form of String
     */
    public static String getStackTraceString(Throwable tr) {
        String result = "";
        if (tr == null) {
            return result;
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "UnknownHostException";
            }
            t = t.getCause();
        }
        PrintWriter pw = null;
        try {
            StringWriter sw = new StringWriter();
            pw = new PrintWriter(sw);
            tr.printStackTrace(pw);
            pw.flush();
            result = sw.toString();
        } finally {
            close(pw);
        }
        return result;
    }


    public static void close(Closeable closeable) {
        try {
            if (null != closeable) {
                closeable.close();
            }
        } catch (IOException e) {
        }
    }


    public static String toString(Object object) {
        if (object == null) {
            return "null";
        }
        if (!object.getClass().isArray()) {
            return object.toString();
        }
        if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        }
        if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object);
        }
        if (object instanceof char[]) {
            return Arrays.toString((char[]) object);
        }
        if (object instanceof short[]) {
            return Arrays.toString((short[]) object);
        }
        if (object instanceof int[]) {
            return Arrays.toString((int[]) object);
        }
        if (object instanceof long[]) {
            return Arrays.toString((long[]) object);
        }
        if (object instanceof float[]) {
            return Arrays.toString((float[]) object);
        }
        if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        }
        if (object instanceof Object[]) {
            return Arrays.deepToString((Object[]) object);
        }
        return "Couldn't find a correct type for the object";
    }

    private static final String TEMPLATE_DATE = "yyyyMMdd";

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(TEMPLATE_DATE);
        return sdf.format(System.currentTimeMillis());
    }

    /**
     * get suitable files from path depend on pack num ,clear redundant files
     *
     * @param path          source files path
     * @param expiredPeriod expired file period
     */
    public static Collection<File> getSuitableFilesWithClear(String path, int expiredPeriod) {
        Collection<File> files = new CopyOnWriteArrayList<>();
        File file = new File(path);
        File[] subFile = file.listFiles();

        if (null == subFile)
            return files;

        for (int index = 0; index < subFile.length; index++) {
            if (!subFile[index].isDirectory()) {
                File item = subFile[index];

                long expired = expiredPeriod * 24 * 60 * 60 * 1000L;
                if ((System.currentTimeMillis() - item.lastModified() > expired) && !item.delete())
                    com.carson.quicker.logger.QLogger.error("can not delete expired file " + item.getName());
                if (item.getName().endsWith(SUFFIX_LOCK) ||
                        item.getName().endsWith(SUFFIX_ZIP))
                    continue;
                files.add(item);
            }
        }
        return files;
    }

    /**
     * zip files
     *
     * @param resFileList zip from files
     * @param zipFile     zip to file
     * @param comment     comment of target file
     */
    public static boolean zipFiles(Collection<File> resFileList, File zipFile, String comment)
            throws IOException {
        if (null == resFileList || resFileList.isEmpty())
            return false;

        ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
                zipFile), BUFF_SIZE));
        for (File resFile : resFileList) {
            zipFile(resFile, zipOutputStream, "");
        }
        if (!TextUtils.isEmpty(comment))
            zipOutputStream.setComment(comment);
        zipOutputStream.close();
        return true;
    }

    /**
     * zip file
     *
     * @param resFile  zip from file
     * @param zipOut   zip to file
     * @param rootPath target file path
     */
    private static void zipFile(File resFile, ZipOutputStream zipOut, String rootPath)
            throws IOException {
        String filePath = rootPath + (rootPath.trim().length() == 0 ? "" : File.separator)
                + resFile.getName();
        filePath = new String(filePath.getBytes("8859_1"), "GB2312");
        if (resFile.isDirectory()) {
            File[] fileList = resFile.listFiles();
            for (File file : fileList) {
                zipFile(file, zipOut, filePath);
            }
        } else {
            byte[] buffer = new byte[BUFF_SIZE];
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile),
                    BUFF_SIZE);
            zipOut.putNextEntry(new ZipEntry(filePath));
            int realLength;
            while ((realLength = in.read(buffer)) != -1) {
                zipOut.write(buffer, 0, realLength);
            }
            in.close();
            zipOut.flush();
            zipOut.closeEntry();
        }
    }
}
