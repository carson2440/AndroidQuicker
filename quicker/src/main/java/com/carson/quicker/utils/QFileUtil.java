package com.carson.quicker.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class QFileUtil {

    /**
     * 判断文件是否存在
     *
     * @param file
     * @return
     */
    private boolean exists(File file) {
        if (QStrings.isNotEmpty(file)) {
            if (file.isFile()) {
                return file.exists();
            }
        }
        return false;
    }

    /**
     * 获取文件后缀
     *
     * @param fileName
     * @return
     */
    public static String getSuffix(String fileName) {
        int point = fileName.lastIndexOf('.');
        int length = fileName.length();
        if (point == -1 || point == length - 1) {
            return "";
        } else {
            return fileName.substring(point + 1, length);
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
                    throw new FileNotFoundException("File don't exist: " + file);
                }
                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }
}
