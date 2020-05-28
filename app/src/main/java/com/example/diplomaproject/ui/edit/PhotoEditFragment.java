package com.example.diplomaproject.ui.edit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diplomaproject.R;
import com.example.diplomaproject.ui.MainActivity;

public class PhotoEditFragment extends Fragment {

    private MainActivity mMaiActivity;

    public static PhotoEditFragment newInstance() {
        return new PhotoEditFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View photoEditFragment = inflater.inflate(R.layout.fragment_photo_edit, container, false);
        mMaiActivity = (MainActivity) getActivity();

        ImageView imageView = photoEditFragment.findViewById(R.id.photoView);
        Bundle bundle = getArguments();
        if(bundle != null) {
            byte[] byteArray =bundle.getByteArray("facesBitmap");
            if(byteArray != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                imageView.setImageBitmap(bmp);
            }
        }

        return photoEditFragment;
    }
}
