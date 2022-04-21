package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import com.tutuanle.chatapp.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    FirebaseAuth auth;

    private ActivitySignInBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.textCreateNewAccount.setOnClickListener( v ->
                startActivity( new Intent(getApplicationContext(), SignUpActivity.class)));
    }
}