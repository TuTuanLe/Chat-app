package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tutuanle.chatapp.databinding.ActivityInfomationBinding;

public class InformationActivity extends AppCompatActivity {
    ActivityInfomationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfomationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}