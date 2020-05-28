package com.example.diplomaproject.ui;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.diplomaproject.R;
import com.example.diplomaproject.ui.edit.PhotoEditFragment;
import com.example.diplomaproject.ui.main.MainFragment;

import org.opencv.android.OpenCVLoader;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressBar mProgressBar;

    private interface OpenCVTaskListener {
        void onOpenCVLoaded();
    }

    private OpenCVTaskListener mOpenCVTaskListener = new OpenCVTaskListener() {
        @Override
        public void onOpenCVLoaded() {
            mProgressBar.setVisibility(View.GONE);
            createMainFragment();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.progressBar);

        new OpenCVLoaderTask(mOpenCVTaskListener).execute();
    }

    private void createMainFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, MainFragment.newInstance())
                .commit();
    }

    public void createPhotoEditFragment(Bitmap facesBitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        facesBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Bundle bundle = new Bundle();
        bundle.putByteArray("facesBitmap", byteArray);

        PhotoEditFragment photoEditFragment = PhotoEditFragment.newInstance();
        photoEditFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, photoEditFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count < 1) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private static class OpenCVLoaderTask extends AsyncTask<Void, Void, Void> {

        private OpenCVTaskListener mOpenCVTaskListener;

        OpenCVLoaderTask(OpenCVTaskListener openCVTaskListener) {
            mOpenCVTaskListener = openCVTaskListener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            OpenCVLoader.initDebug();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (mOpenCVTaskListener != null) {
                mOpenCVTaskListener.onOpenCVLoaded();
            }
        }
    }
}
