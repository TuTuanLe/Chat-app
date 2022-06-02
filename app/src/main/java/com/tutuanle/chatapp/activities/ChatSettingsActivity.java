package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.tutuanle.chatapp.databinding.ActivityChatScreenBinding;
import com.tutuanle.chatapp.databinding.ActivityChatSettingsBinding;

public class ChatSettingsActivity extends AppCompatActivity {
    ActivityChatSettingsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.imageBack.setOnClickListener(v->onBackPressed());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}