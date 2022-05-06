package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tutuanle.chatapp.databinding.ActivityOtpscreenBinding;

public class OTPScreenActivity extends AppCompatActivity {
    private ActivityOtpscreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}