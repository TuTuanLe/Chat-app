package com.tutuanle.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.tutuanle.chatapp.databinding.ActivityMyPhoneNumberBinding;

import java.util.concurrent.TimeUnit;

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

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84"+ binding.phoneBox.getText().toString(),
                60,
                TimeUnit.SECONDS,
                MyPhoneNumberActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        loading(false);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                    }
                }
        );


        Intent intent = new Intent(getApplicationContext(), OTPScreenActivity.class);
        intent.putExtra("phoneNumber", binding.phoneBox.getText().toString());
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