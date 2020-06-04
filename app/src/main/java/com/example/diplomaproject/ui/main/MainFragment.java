package com.example.diplomaproject.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.diplomaproject.R;
import com.example.diplomaproject.face.FaceDetector;
import com.example.diplomaproject.ui.MainActivity;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;

public class MainFragment extends Fragment {

    private static final int WRITE_READ_PERMISSIONS_REQUEST_CODE = 0;
    private static final String[] WRITE_READ_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int PICK_IMAGE_REQUEST_CODE_FACE = 1;
    private static final int PICK_IMAGE_REQUEST_CODE_RESOLUTION = 2;

    private FaceDetector mFaceDetector = new FaceDetector();

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private MainActivity mMaiActivity;

    private ProgressBar mProgressBar;
    private MaterialButton mFaceClassificatorButton;
    private MaterialButton mSuperResolutionButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_main, container, false);

        mMaiActivity = (MainActivity) getActivity();

        mProgressBar = mainView.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        mFaceClassificatorButton = mainView.findViewById(R.id.faceClassificatorButton);
        mFaceClassificatorButton.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(mMaiActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(WRITE_READ_PERMISSIONS, WRITE_READ_PERMISSIONS_REQUEST_CODE);
            } else {
                pickImage(PICK_IMAGE_REQUEST_CODE_FACE);
            }
        });

        mSuperResolutionButton = mainView.findViewById(R.id.superResolutionButton);
        mSuperResolutionButton.setOnClickListener(view ->{
            if (ContextCompat.checkSelfPermission(mMaiActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(WRITE_READ_PERMISSIONS, WRITE_READ_PERMISSIONS_REQUEST_CODE);
            } else {
                pickImage(PICK_IMAGE_REQUEST_CODE_RESOLUTION);
            }
        });

        return mainView;
    }

    private void pickImage(int code) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Please select picture"), code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE_FACE) {
            if (data != null) {
                Uri imageUri = data.getData();
                Bitmap imageBitmap;

                if (imageUri != null) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            imageBitmap =
                                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(mMaiActivity.getContentResolver(), imageUri));
                        } else {
                            imageBitmap =
                                    BitmapFactory.decodeStream(mMaiActivity.getContentResolver().openInputStream(imageUri));
                        }

                        mProgressBar.setVisibility(View.VISIBLE);
                        mFaceClassificatorButton.setVisibility(View.GONE);
                        mSuperResolutionButton.setVisibility(View.GONE);
                        mFaceDetector.detectFace(imageBitmap, (facesBitmap, facesBound) -> {
                            mMaiActivity.createPhotoEditFragment(facesBitmap, facesBound);
                            mProgressBar.setVisibility(View.GONE);
                            mFaceClassificatorButton.setVisibility(View.GONE);
                            mSuperResolutionButton.setVisibility(View.GONE);
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST_CODE_RESOLUTION) {
            if (data != null) {
                Uri imageUri = data.getData();
                Bitmap imageBitmap;

                if (imageUri != null) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            imageBitmap =
                                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(mMaiActivity.getContentResolver(), imageUri));
                        } else {
                            imageBitmap =
                                    BitmapFactory.decodeStream(mMaiActivity.getContentResolver().openInputStream(imageUri));
                        }

                        mProgressBar.setVisibility(View.VISIBLE);
                        mFaceClassificatorButton.setVisibility(View.GONE);
                        mFaceDetector.detectFace(imageBitmap, (facesBitmap, facesBound) -> {
                            mMaiActivity.createPhotoEditFragment(facesBitmap, facesBound);
                            mProgressBar.setVisibility(View.GONE);
                            mFaceClassificatorButton.setVisibility(View.GONE);
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
