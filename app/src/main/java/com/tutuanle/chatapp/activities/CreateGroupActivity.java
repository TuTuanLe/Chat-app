package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tutuanle.chatapp.databinding.ActivityCreateGroupBinding;

public class CreateGroupActivity extends AppCompatActivity {
    ActivityCreateGroupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityCreateGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}