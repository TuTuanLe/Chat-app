package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.tutuanle.chatapp.databinding.ActivityShowImageBinding;
import com.tutuanle.chatapp.utilities.Constants;

public class ShowImageActivity extends AppCompatActivity {
    ActivityShowImageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String imageBitmap  = getIntent().getStringExtra(Constants.KEY_IMAGE);
        binding.showImage.setImageBitmap(getBitmapFromEnCodedString(imageBitmap));

    }

    private Bitmap getBitmapFromEnCodedString(String enCodedImage) {
        byte[] bytes = Base64.decode(enCodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}