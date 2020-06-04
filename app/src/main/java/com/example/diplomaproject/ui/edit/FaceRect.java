package com.example.diplomaproject.ui.edit;

import android.os.Parcel;
import android.os.Parcelable;

public class FaceRect implements Parcelable {
    private int mX;
    private int mY;
    private int mHeight;
    private int mWight;

    public FaceRect(int x, int y, int height, int wight) {
        mX = x;
        mY = y;
        mHeight = height;
        mWight = wight;
    }

    public FaceRect(Parcel in) {
        mX = in.readInt();
        mY = in.readInt();
        mHeight = in.readInt();
        mWight = in.readInt();
    }

    public static final Creator<FaceRect> CREATOR = new Creator<FaceRect>() {
        @Override
        public FaceRect createFromParcel(Parcel in) {
            return new FaceRect(in);
        }

        @Override
        public FaceRect[] newArray(int size) {
            return new FaceRect[size];
        }
    };

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mX);
        dest.writeInt(mY);
        dest.writeInt(mHeight);
        dest.writeInt(mWight);
    }
}
