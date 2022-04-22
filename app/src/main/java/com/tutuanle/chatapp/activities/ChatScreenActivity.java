package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tutuanle.chatapp.databinding.ActivityChatScreenBinding;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;

public class ChatScreenActivity extends AppCompatActivity {
    private ActivityChatScreenBinding binding;
    private User receiverUSer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadReceiverDetails();
        setListener();
    }

    private void loadReceiverDetails() {
        receiverUSer = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUSer.getName());

    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
}