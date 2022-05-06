package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tutuanle.chatapp.databinding.ActivityMyPhoneNumberBinding;

public class MyPhoneNumberActivity extends AppCompatActivity {
    private ActivityMyPhoneNumberBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}