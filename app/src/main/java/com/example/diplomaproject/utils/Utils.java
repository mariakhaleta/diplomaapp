package com.example.diplomaproject.utils;

import android.graphics.Bitmap;

import org.opencv.core.Mat;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static Mat bitmapToMat(Bitmap imageBitmap) {
        Mat imageMat = new Mat();
        Bitmap imageBitmap32 = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        org.opencv.android.Utils.bitmapToMat(imageBitmap32, imageMat);
        return imageMat;
    }
}
