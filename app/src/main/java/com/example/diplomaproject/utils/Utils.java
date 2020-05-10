package com.example.diplomaproject.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static Mat bitmapToMat(Bitmap imageBitmap) {
        Mat imageMat = new Mat();
        Bitmap imageBitmap32 = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        org.opencv.android.Utils.bitmapToMat(imageBitmap32, imageMat);
        return imageMat;
    }

    public static void copyAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e(TAG, "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File("sdcard", filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch (IOException e) {
                Log.e(TAG, "Failed to copy asset file: " + filename, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        if (e.getLocalizedMessage() != null) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        if (e.getLocalizedMessage() != null) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }
                    }
                }
            }
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
