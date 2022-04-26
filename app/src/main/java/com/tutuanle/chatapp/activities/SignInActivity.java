package com.tutuanle.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tutuanle.chatapp.databinding.ActivitySignInBinding;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SignInActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private PreferenceManager preferenceManager;
    private ActivitySignInBinding binding;
    private GoogleSignInClient mGoogleSignInClient;
    static final int RC_SIGN_IN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(binding.getRoot());
        setListeners();
        initLoginWithGoogle();

    }

    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        binding.buttonSignIn.setOnClickListener(v -> {
                    if (isValidSignInDetail()) {
                        signIn();
                    }
                }
        );
        binding.buttonLoginWithGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signIn() {
        loading(true);


        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, snapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, snapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, snapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Unable to sign in ");
                    }
                });
    }

    private Boolean isValidSignInDetail() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Email");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter valid Email");
            return false;
        }
        return true;
    }

    private void loading(@NonNull Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);

        } else {
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }


    private void initLoginWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }



    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            showToast(account.getId());
            loading(true);
            String imageTest = "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";
//            String inputName, String email, String password, String encodedImage
            signUpGoogle(account.getGivenName(),account.getEmail(), account.getEmail(),imageTest );


//            Intent intent = new Intent(this, infoGoogleAcc.class);
//            startActivity(intent);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private int checkEmail(String email, String password) {
        AtomicInteger rs = new AtomicInteger(-1);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .whereEqualTo(Constants.KEY_PASSWORD, password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, snapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, snapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, snapshot.getString(Constants.KEY_IMAGE));
                        rs.set(0);

                    }
                });
        Log.d("Test_id", rs.toString());
        return rs.get();
    }


    private void signUpGoogle(String inputName, String email, String password, String encodedImage) {
        loading(true);
        if (checkEmail(email, password) == 0) {
            Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            HashMap<String, Object> user = new HashMap<>();
            user.put(Constants.KEY_NAME, inputName);
            user.put(Constants.KEY_EMAIL, email);
            user.put(Constants.KEY_PASSWORD, password);
            user.put(Constants.KEY_IMAGE, encodedImage);
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .add(user)
                    .addOnSuccessListener(documentReference -> {
                        loading(false);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                        preferenceManager.putString(Constants.KEY_NAME, inputName);
                        preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                        Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .addOnFailureListener(exception -> {
                        loading(false);
                        showToast(exception.getMessage());
                    });
        }


    }
    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


}