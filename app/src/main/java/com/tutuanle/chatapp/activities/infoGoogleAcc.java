package com.tutuanle.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.databinding.ActivityInfoGoogleAccBinding;

public class infoGoogleAcc extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private ActivityInfoGoogleAccBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoGoogleAccBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // check null and show account Google signed information
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            binding.txtDisplayName.setText("display name" +acct.getDisplayName());
            binding.txtEmail.setText("email : "+acct.getEmail());
            binding.txtId.setText("id : "+acct.getId());
            binding.txtGivenName.setText("GivenName : "+acct.getGivenName());
            Glide.with(this).load(acct.getPhotoUrl()).into(binding.imgAvatar);
            binding.txtImg.setText("img uri : "+ acct.getPhotoUrl());
        }


    }

    // signout  account GG when click btn Logout
    public void onClick(View v) {
        switch (v.getId()) {
            // ...
            case R.id.btnLogoutGG:
                signOut();
                break;
            // ...
        }
    };

    // after click logout show Sigin Screen
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(intent);
                    }
                });
    }
}