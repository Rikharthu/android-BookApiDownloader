package com.example.uberv.maptbookapidownloader;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import timber.log.Timber;

public class FileUtils {

    public static final String BOOKS_FOLDER_NAME = "Mapt Books";

    public static File createBookFile(String directoryName, String fileName) {
        // Get the directory for the user's public pictures directory.
        File rootPath = new File(Environment.getExternalStorageDirectory(), BOOKS_FOLDER_NAME + File.separator + directoryName);
        if (!rootPath.exists()) {
            if (!rootPath.mkdirs()) {
                Timber.d("Could not create file " + rootPath.toString());
                return null;
            }
        }
        // Create a file reference
        return new File(rootPath, fileName);
    }

    public static File createDirectory(String directory) {
        // Get the directory for the user's public pictures directory.
        File rootPath = new File(Environment.getExternalStorageDirectory(), directory);
        if (!rootPath.exists()) {
            if (!rootPath.mkdirs()) {
                Timber.d("Could not create file " + rootPath.toString());
                return null;
            }
        }
        return rootPath;
    }

    public static void saveImageToFile(Bitmap bmp, String bookFileDirectory, String filename) {
        File file = new File(createDirectory(BOOKS_FOLDER_NAME + File.separator + bookFileDirectory + File.separator + "graphics"), filename);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            Timber.d("saved " + filename);
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean writeToFile(File file, String data) {
        //Create a new file and write some data
        try {
            FileOutputStream mOutput = new FileOutputStream(file, false);
            mOutput.write(data.getBytes());
            mOutput.flush();
            //With external files, it is often good to wait for the write
            mOutput.getFD().sync();
            mOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void zip(String[] _files, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[1024];

            for (int i = 0; i < _files.length; i++) {
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, 1024);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, 1024)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
