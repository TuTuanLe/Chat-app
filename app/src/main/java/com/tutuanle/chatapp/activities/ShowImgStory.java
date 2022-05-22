package com.tutuanle.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.fragment.StoryFragment;
import com.tutuanle.chatapp.models.Status;
import com.tutuanle.chatapp.models.Story;
import com.tutuanle.chatapp.models.UserStatus;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.time.LocalDateTime;
import java.util.UUID;

public class ShowImgStory extends AppCompatActivity {

    private ImageView imgStory;
    private Button btnUpStory;
    private String encodedImage;
    private ImageButton ibtnBack;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_img_story);


        imgStory = findViewById(R.id.imgStory);
        btnUpStory = findViewById(R.id.btnUpStory);
        ibtnBack = findViewById(R.id.ibtnBack);
        Intent intent = getIntent();
        String uriString = intent.getStringExtra("img");
        String userId = intent.getStringExtra("userId");
        Uri uriImg = Uri.parse(uriString);

        imgStory.setImageURI(uriImg);
        btnUpStory.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                try {
                    InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(uriImg);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    encodedImage = encodeImage(bitmap);

//                    FirebaseStorage storage = FirebaseStorage.getInstance();
//                    Date date = new Date();
//                    StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");
//                    reference.putFile(uriImg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                            if (task.isSuccessful()) {
//                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                    @Override
//                                    public void onSuccess(Uri uri) {
//                                        imageUrl = uri.toString();
//                                    }
//                                });
//                            }
//                        }
//                    });

                    String dateNow = getDateNow();
                    Story story = new Story(UUID.randomUUID().toString(),userId,encodedImage,dateNow);
                    FirebaseFirestore.getInstance()
                    .collection(Constants.KEY_COLLECTION_SOTRY)
                    .document(story.storyId).set(story);
                    onBackPressed();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getDateNow() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dtf.format(now);
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