package com.example.diplomaproject.face;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

public class FaceDetector {

    private FirebaseVisionFaceDetector mFirebaseVisionFaceDetector;

    public FaceDetector() {
        FirebaseVisionFaceDetectorOptions faceDetectorOptions =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                        .build();

        mFirebaseVisionFaceDetector = FirebaseVision.getInstance()
                .getVisionFaceDetector(faceDetectorOptions);
    }

    public void detectFace(Bitmap imageBitmap, FaceDetectorListener listener) {
        final Bitmap scaledBitmap = getScaledBitmap(imageBitmap);
        Canvas canvas = new Canvas(scaledBitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(scaledBitmap);
        mFirebaseVisionFaceDetector.detectInImage(image).addOnSuccessListener(faces -> {
            faces.forEach(face -> {
                Rect faceRect = face.getBoundingBox();
                canvas.drawRect(faceRect, paint);
            });
            listener.onFaceDetected(scaledBitmap);
        }).addOnFailureListener(System.out::println);
    }

    public interface FaceDetectorListener {
        void onFaceDetected(Bitmap bitmap);
    }

    private Bitmap getScaledBitmap(Bitmap bitmap) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        float aspectRatio = mutableBitmap.getWidth() / (float) mutableBitmap.getHeight();
        int width = 480;
        int height = Math.round(width / aspectRatio);
        return Bitmap.createScaledBitmap(mutableBitmap, width, height, false);
    }
}
