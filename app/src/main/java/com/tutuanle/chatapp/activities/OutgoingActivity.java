package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tutuanle.chatapp.databinding.ActivityOutgoingBinding;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;

public class OutgoingActivity extends AppCompatActivity {
    ActivityOutgoingBinding binding;
    private  User receiverUSer;
    private  String meetingType ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOutgoingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupCall();
    }

    private void setupCall(){
        receiverUSer = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        meetingType = getIntent().getStringExtra("type_call");
        if(meetingType != null){

        }
        binding.imageStop.setOnClickListener(v->onBackPressed());
    }
}