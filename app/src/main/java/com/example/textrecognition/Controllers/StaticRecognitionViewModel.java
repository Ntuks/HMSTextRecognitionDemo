package com.example.textrecognition.Controllers;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.ViewModel;

public class StaticRecognitionViewModel extends ViewModel {
    private Uri imageURI;
    private Bitmap imageBitmap;

    public Uri getImageURI() {
        return imageURI;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public void setImageURI(Uri imageLocation) {
        this.imageURI = imageLocation;
    }
}
