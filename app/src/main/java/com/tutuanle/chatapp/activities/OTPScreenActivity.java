package com.tutuanle.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.tutuanle.chatapp.databinding.ActivityOtpscreenBinding;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;


public class OTPScreenActivity extends AppCompatActivity {
    private ActivityOtpscreenBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpscreenBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());

        setUpOTPInput();
        getAgainOTP();
        setListener();

    }

    private void setListener() {
        binding.goBack.setOnClickListener(v -> onBackPressed());
    }

    private void setUpOTPInput() {

        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String verificationId = getIntent().getStringExtra("verificationId");
        User user = (User) getIntent().getSerializableExtra(Constants.KEY_USER);

        binding.textView.setText("Check your SMS messages , we have sent your pin at + " + phoneNumber);
        binding.inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    binding.inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    binding.inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    binding.inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    binding.inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    binding.inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.inputCode6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    binding.inputCode6.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    loading(true);
                    String code = binding.inputCode1.getText().toString() +
                            binding.inputCode2.getText().toString() +
                            binding.inputCode3.getText().toString() +
                            binding.inputCode4.getText().toString() +
                            binding.inputCode5.getText().toString() +
                            binding.inputCode6.getText().toString();
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code);

                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnSuccessListener(v -> {
                                Toast.makeText(OTPScreenActivity.this, "login successfully", Toast.LENGTH_SHORT).show();

                                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                preferenceManager.putString(Constants.KEY_USER_ID, user.getUid());
                                preferenceManager.putString(Constants.KEY_NAME, user.getName());
                                preferenceManager.putString(Constants.KEY_IMAGE, user.getProfileImage());
                                preferenceManager.putString(Constants.KEY_EMAIL, user.getEmail());
                                preferenceManager.putString(Constants.KEY_NUMBER_PHONE, user.getPhoneNumber());
                                preferenceManager.putString(Constants.KEY_PASSWORD, user.getPassword());
                                Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                loading(false);
                            })
                            .addOnFailureListener(v -> {
                                Toast.makeText(OTPScreenActivity.this, "get OTP fail", Toast.LENGTH_SHORT).show();
                                loading(false);
                            })
                    ;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getAgainOTP() {
        binding.textResendOTP.setOnClickListener(v -> {
            binding.inputCode1.setText("");
            binding.inputCode2.setText("");
            binding.inputCode3.setText("");
            binding.inputCode4.setText("");
            binding.inputCode5.setText("");
            binding.inputCode6.setText("");
        });
    }

    private void loading(@NonNull Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);

        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}