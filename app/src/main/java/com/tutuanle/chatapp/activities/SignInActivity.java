package com.tutuanle.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.databinding.ActivitySignInBinding;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.ConvertImageUrlToBitmap;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;


public class SignInActivity extends AppCompatActivity {

    FirebaseAuth auth;
    private PreferenceManager preferenceManager;
    private ActivitySignInBinding binding;
    private GoogleSignInClient mGoogleSignInClient;
    static final int RC_SIGN_IN = 0;
    private int check = 1;

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
        initLoginWithGoogle();
        setListeners();

    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name =  "chat_name";
            String description = "chat_des";
            int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("login_123", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "login_123")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("textTitle")
                .setContentText("tesst 123")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        createNotificationChannel();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);




// notificationId is a unique int for each notification that you must define
        notificationManager.notify(123, builder.build());
    }


    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        binding.buttonSignIn.setOnClickListener(v -> {
//            showNotification();
                    if (isValidSignInDetail()) {
                        signIn();
                    }
                }
        );
        binding.buttonLoginWithGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        binding.forgetPassword.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MyPhoneNumberActivity.class));
        });

        binding.passwordHint.setOnClickListener(v -> {
            if (check == 0) {
                binding.passwordHint.setImageResource(R.drawable.eye);
                binding.inputPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                check = 1;


            } else {
                binding.passwordHint.setImageResource(R.drawable.icons8eye24);
                check = 0;
                binding.inputPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

            }

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
                        preferenceManager.putString(Constants.KEY_EMAIL, snapshot.getString(Constants.KEY_EMAIL));
                        preferenceManager.putString(Constants.KEY_NUMBER_PHONE, snapshot.getString(Constants.KEY_NUMBER_PHONE));
                        preferenceManager.putString(Constants.KEY_PASSWORD, snapshot.getString(Constants.KEY_PASSWORD));
                        Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Email or password not match !!! ");
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


    public static Bitmap getBitmapFromURL(Uri src) {
        try {
            URL url = new URL(src.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            // Log exception
            return null;
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

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            loading(true);

            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Bitmap temp = getBitmapFromURL(Objects.requireNonNull(account.getPhotoUrl()));
                assert temp != null;

                signUpGoogle(account.getDisplayName(), account.getEmail(), account.getEmail(), encodeImage(temp));

            } catch (Exception e) {
                Log.d("handleSignInResult", e.toString());
            }


        } catch (ApiException e) {
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }


    private void signUpGoogle(String inputName, String email, String password, String encodedImage) {
        loading(true);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, snapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, snapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE, snapshot.getString(Constants.KEY_IMAGE));
                        preferenceManager.putString(Constants.KEY_EMAIL, snapshot.getString(Constants.KEY_EMAIL));
                        preferenceManager.putString(Constants.KEY_NUMBER_PHONE, snapshot.getString(Constants.KEY_NUMBER_PHONE));
                        preferenceManager.putString(Constants.KEY_PASSWORD, snapshot.getString(Constants.KEY_PASSWORD));
                        Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
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
                });


    }


}