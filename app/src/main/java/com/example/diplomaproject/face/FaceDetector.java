package com.example.diplomaproject.face;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.diplomaproject.ui.edit.FaceRect;
import com.example.diplomaproject.utils.Utils;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.ArrayList;

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
        final Bitmap scaledBitmap = Utils.getScaledBitmap(imageBitmap);

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(scaledBitmap);
        mFirebaseVisionFaceDetector.detectInImage(image).addOnSuccessListener(faces -> {
            ArrayList<FaceRect> facesBound = new ArrayList<>();
            Canvas canvas = new Canvas(scaledBitmap);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(5);

            faces.forEach(face -> {
                Rect boundingBox = face.getBoundingBox();

                facesBound.add(new FaceRect(boundingBox.left, boundingBox.top, boundingBox.height(), boundingBox.width()));
                canvas.drawRect(boundingBox, paint);
            });

            listener.onFaceDetected(scaledBitmap, facesBound);
        }).addOnFailureListener(System.out::println);
    }

    public interface FaceDetectorListener {
        void onFaceDetected(Bitmap bitmap, ArrayList<FaceRect> facesBound);
    }
}
