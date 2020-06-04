package com.example.diplomaproject.utils;

import android.graphics.Bitmap;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static Mat bitmapToMat(Bitmap imageBitmap) {
        Mat imageMat = new Mat();
        Bitmap imageBitmap32 = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        org.opencv.android.Utils.bitmapToMat(imageBitmap32, imageMat);
        return imageMat;
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        float aspectRatio = mutableBitmap.getWidth() / (float) mutableBitmap.getHeight();
        int width = 1000;
        int height = Math.round(width / aspectRatio);
        return Bitmap.createScaledBitmap(mutableBitmap, width, height, false);
    }
}
