package com.example.diplomaproject.face;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.diplomaproject.utils.Utils;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

public class FaceDetector {

    private CascadeClassifier mCascadeClassifier;

    public FaceDetector() {
        mCascadeClassifier = new CascadeClassifier();
        mCascadeClassifier.load("sdcard/haarcascade_frontalface_default.xml");
    }

    public Bitmap detectFace(Bitmap imageBitmap) {
        Mat imageMat = Utils.bitmapToMat(imageBitmap);
        Mat grayImageMat = new Mat();
        Imgproc.cvtColor(imageMat, grayImageMat, Imgproc.COLOR_BGR2GRAY);

        int absoluteFaceSize = Math.round(grayImageMat.height() * 0.2f);

        MatOfRect detectionResult = new MatOfRect();
        mCascadeClassifier.detectMultiScale(grayImageMat, detectionResult, 1.1, 2);

        Bitmap mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);

        for (Rect detectedFace : detectionResult.toArray()) {
            canvas.drawRect(
                    new android.graphics.Rect(
                            detectedFace.x,
                            detectedFace.y,
                            detectedFace.x + detectedFace.width,
                            detectedFace.y + detectedFace.height),
                    paint);
        }

        return mutableBitmap;
    }
}
