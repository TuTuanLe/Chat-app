package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tutuanle.chatapp.databinding.ActivityGroupCsreenBinding;

public class GroupScreenActivity extends AppCompatActivity {
    private ActivityGroupCsreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupCsreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}