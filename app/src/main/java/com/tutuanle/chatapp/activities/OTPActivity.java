package com.tutuanle.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;
import com.tutuanle.chatapp.databinding.ActivityOtpactivityBinding;

import java.util.concurrent.TimeUnit;


public class OTPActivity extends AppCompatActivity {
    ActivityOtpactivityBinding binding;
    FirebaseAuth auth;
    String verificationId ;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Send OTP...");
        dialog.setCancelable(false);
        dialog.show();

        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        binding.textView.setText("Verify " + phoneNumber);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(OTPActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyID, forceResendingToken);
                        verificationId= verifyID;
                        dialog.dismiss();
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                auth.signInWithCredential(credential).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Intent intent = new Intent(OTPActivity.this, SetupProfileActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }else{
                        Toast.makeText(OTPActivity.this,"Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}