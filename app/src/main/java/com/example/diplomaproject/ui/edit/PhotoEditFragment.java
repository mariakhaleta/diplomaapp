package com.example.diplomaproject.ui.edit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diplomaproject.R;
import com.example.diplomaproject.ui.MainActivity;
import com.example.diplomaproject.utils.Utils;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class PhotoEditFragment extends Fragment {

    public static final String FACES_BITMAP = "facesBitmap";
    public static final String FACES_BOUND = "facesBound";

    private static final int OPENCV_BLUR_COEF = 55;

    private MainActivity mMainActivity;

    public static PhotoEditFragment newInstance() {
        return new PhotoEditFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View photoEditFragment = inflater.inflate(R.layout.fragment_photo_edit, container, false);
        mMainActivity = (MainActivity) getActivity();

        ImageView imageView = photoEditFragment.findViewById(R.id.photoView);
        Bundle bundle = getArguments();
        final ArrayList<FaceRect> facesBound = new ArrayList<>();
        Bitmap imageBitmap = null;

        if (bundle != null) {
            byte[] byteArray = bundle.getByteArray(FACES_BITMAP);
            if (byteArray != null) {
                imageBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                imageView.setImageBitmap(imageBitmap);
            }

            ArrayList<FaceRect> facesBoundBundle = bundle.getParcelableArrayList(FACES_BOUND);
            if(facesBoundBundle != null) {
                facesBound.addAll(facesBoundBundle);
            }
        }

        if(imageBitmap != null) {
            Mat imageMat = Utils.bitmapToMat(imageBitmap);

            List<Point> points = new ArrayList<>();
            facesBound.forEach(faceRect -> points.add(new Point(faceRect.getX(), faceRect.getY())));

            MatOfPoint matOfPoint = new MatOfPoint();
            matOfPoint.fromList(points);

            imageView.setOnTouchListener((v, event) -> {
                final double evX = event.getX();
                final double evY = event.getY();

                if (facesBound != null) {

                    facesBound.forEach(faceRect -> {
                        Rect rect = new Rect(faceRect.getX(), faceRect.getY(), faceRect.getWidth(), faceRect.getHeight());
                        if (rect.contains(new Point(evX, evY))) {
                            Mat mask = imageMat.submat(rect);

                            Imgproc.GaussianBlur(mask, mask, new Size(OPENCV_BLUR_COEF, OPENCV_BLUR_COEF), OPENCV_BLUR_COEF);
                            Bitmap bluredBitmap = Bitmap.createBitmap(imageMat.cols(), imageMat.rows(), Bitmap.Config.ARGB_8888);
                            org.opencv.android.Utils.matToBitmap(imageMat, bluredBitmap);
                            imageView.setImageBitmap(bluredBitmap);
                        }
                    });
                }

                return true;
            });
        }

        return photoEditFragment;
    }
}
