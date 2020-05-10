package com.example.diplomaproject.ui.main;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.diplomaproject.R;
import com.example.diplomaproject.face.FaceDetector;
import com.example.diplomaproject.ui.MainActivity;
import com.example.diplomaproject.utils.Utils;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.io.InputStream;

public class MainFragment extends Fragment {

    private static final int WRITE_READ_PERMISSIONS_REQUEST_CODE = 0;
    private static final String[] WRITE_READ_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int PICK_IMAGE_REQUEST_CODE = 1;

    private FaceDetector mFaceDetector = new FaceDetector();

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private MainActivity mMaiActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_main, container, false);

        mMaiActivity = (MainActivity) getActivity();

        MaterialButton faceClassificatorButton = mainView.findViewById(R.id.faceClassificatorButton);
        faceClassificatorButton.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(mMaiActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(WRITE_READ_PERMISSIONS, WRITE_READ_PERMISSIONS_REQUEST_CODE);
            } else {
                pickImage();
            }
        });

        return mainView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case WRITE_READ_PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                } else {
                    Toast.makeText(mMaiActivity, "Permissions was denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            default:
        }
    }

    private void pickImage() {
        Utils.copyAssets(mMaiActivity);

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Please select picture"), PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE) {
            if (data != null) {
                Uri imageUri = data.getData();
                Bitmap imageBitmap = null;

                if (imageUri != null) {
                    try {
                        imageBitmap =
                                MediaStore.Images.Media.getBitmap(mMaiActivity.getContentResolver(), imageUri);
                        InputStream inputStream = mMaiActivity.getContentResolver().openInputStream(imageUri);
                        Bitmap rotatedBitmap = correctRotate(inputStream, imageBitmap);

                        Bitmap facesBitmap = mFaceDetector.detectFace(rotatedBitmap);
                        mMaiActivity.createPhotoEditFragment(facesBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Bitmap correctRotate(InputStream inputStream, Bitmap imageBitmap) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ei != null) {
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(imageBitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(imageBitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(imageBitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = imageBitmap;
            }

            return rotatedBitmap;
        }

        return imageBitmap;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause) {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if (cursor != null) {
            boolean moveToFirst = cursor.moveToFirst();
            if (moveToFirst) {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;
                if (uri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Images.Media.DATA;
                }


                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        return ret;
    }
}
