package com.tutuanle.chatapp.activities;



import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.databinding.ActivityProfileBinding;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    private MainScreenActivity mainScreenActivity;
    private PreferenceManager preferenceManager;
    private User currentUser;
    private User updateUser;
    private String encodedImage ="";
    private String currentEncodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUser();
        init();
        UpdateProfile();

    }

    private void loadUser(){
        currentUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.inputName.setText(currentUser.getName());
        byte[] bytes = Base64.decode(currentUser.getProfileImage(),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        currentEncodedImage = encodeImage(bitmap);
        binding.imgAvatar.setImageBitmap(bitmap);
        binding.inputNumberPhone.setText(currentUser.getPassword());

    }

    private void init(){
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }


    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    assert result.getData() != null;
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        binding.imgAvatar.setImageBitmap(bitmap);
                        binding.imageProfile.setVisibility(View.GONE);
                        encodedImage = encodeImage(bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
    );


    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    private void UpdateProfile(){
        binding.btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser = new User();
                updateUser.setUid(currentUser.getUid());
                if (encodedImage.equals("")){
                    updateUser.setProfileImage(currentEncodedImage);
                }else{
                    updateUser.setProfileImage(encodedImage);
                }
                updateUser.setName(binding.inputName.getText().toString());
                updateUser.setPhoneNumber(binding.inputNumberPhone.getText().toString());
                updateUser.setEmail(currentUser.getEmail());
                if(binding.inputCurrentPassword.getText().toString().equals("")){
                    updateUser.setPassword(currentUser.getPassword());
                }else{
                    if(binding.inputCurrentPassword.getText().toString().equals(currentUser.getPassword())){
                        Toast.makeText(getApplicationContext(), "SAI Password", Toast.LENGTH_SHORT).show();
                    }
                }

                updateData();
            }
        });
    }

    private void loading(@NonNull Boolean isLoading) {
        if (isLoading) {
            binding.btnUpdateProfile.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);

        } else {
            binding.btnUpdateProfile.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void updateData() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, updateUser.getName());
        user.put(Constants.KEY_PASSWORD, updateUser.getPassword());
        user.put(Constants.KEY_NUMBER_PHONE, updateUser.getPhoneNumber());
        user.put(Constants.KEY_IMAGE, updateUser.getProfileImage());
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(updateUser.getUid())
                .update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                loading(false);
                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                preferenceManager.putString(Constants.KEY_USER_ID, updateUser.getUid());
                preferenceManager.putString(Constants.KEY_NAME, updateUser.getName());
                preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                preferenceManager.putString(Constants.KEY_NUMBER_PHONE, updateUser.getPhoneNumber());
                preferenceManager.putString(Constants.KEY_PASSWORD, updateUser.getPassword().toString());
                preferenceManager.putString(Constants.KEY_EMAIL,updateUser.getEmail());
                Intent intent = new Intent(getApplicationContext(), MainScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}