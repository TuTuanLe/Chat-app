package com.tutuanle.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tutuanle.chatapp.databinding.ActivityMyPhoneNumberBinding;

public class MyPhoneNumberActivity extends AppCompatActivity {
    private ActivityMyPhoneNumberBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }


    private void setListener() {
        binding.buttonGetNumber.setOnClickListener(v -> getOTPCode());
        binding.goBack.setOnClickListener(v -> onBackPressed()
 );

    }

    private void getOTPCode() {
        loading(true);

        Intent intent = new Intent(getApplicationContext(), OTPScreenActivity.class);
        startActivity(intent);
        loading(false);
    }


    private void loading(@NonNull Boolean isLoading) {
        if (isLoading) {
            binding.buttonGetNumber.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);

        } else {
            binding.buttonGetNumber.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

}