package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tutuanle.chatapp.databinding.ActivityIncomingBinding;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

public class IncomingActivity extends AppCompatActivity {

    ActivityIncomingBinding binding;
    private User receiverUSer;
    private String meetingType;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncomingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupCall();
    }

    private void setupCall() {
        receiverUSer = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        meetingType = getIntent().getStringExtra("type_call");

        preferenceManager = new PreferenceManager(getApplicationContext());


        if (meetingType != null) {

        }

    }
}