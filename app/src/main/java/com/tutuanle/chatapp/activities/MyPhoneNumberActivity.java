package com.tutuanle.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tutuanle.chatapp.databinding.ActivityMyPhoneNumberBinding;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;

import java.util.concurrent.TimeUnit;

public class MyPhoneNumberActivity extends AppCompatActivity {
    private ActivityMyPhoneNumberBinding binding;
    private  User user;
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
        getUserWithNumberPhone();
    }


    private void getUserWithNumberPhone() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_NUMBER_PHONE, binding.phoneBox.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                        user = new User();
                        user.setUid(snapshot.getId());
                        user.setName( snapshot.getString(Constants.KEY_NAME));
                        user.setProfileImage( snapshot.getString(Constants.KEY_IMAGE));
                        getAuthPhone();
                    } else {
                        Toast.makeText(this, "Doesn't exist number phone ...", Toast.LENGTH_SHORT).show();
                        loading(false);
                    }
                });
    }

    private void getAuthPhone(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84"+ binding.phoneBox.getText().toString(),
                60,
                TimeUnit.SECONDS,
                MyPhoneNumberActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        loading(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        loading(false);
                        Toast.makeText(MyPhoneNumberActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        loading(false);
                        Intent intent = new Intent(getApplicationContext(), OTPScreenActivity.class);
                        intent.putExtra("phoneNumber", binding.phoneBox.getText().toString());
                        intent.putExtra("verificationId", s);
                        startActivity(intent);
                    }
                }
        );
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