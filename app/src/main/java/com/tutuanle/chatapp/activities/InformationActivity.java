package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.tutuanle.chatapp.adapters.InformationAdapter;
import com.tutuanle.chatapp.databinding.ActivityInfomationBinding;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InformationActivity extends AppCompatActivity {
    ActivityInfomationBinding binding;
    private User receiverUSer;
    private List<String> images;
    private InformationAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfomationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBinding();
        initData();
        getImages();
    }

    private void setBinding() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());

    }

    private void initData() {

        images = new ArrayList<>();
        adapter = new InformationAdapter(images);

        receiverUSer = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.statusList.setLayoutManager(new GridLayoutManager(this , 3));
        binding.statusList.setAdapter(adapter);

        binding.txtUserName.setText(receiverUSer.getName());
        binding.txtEmail.setText(receiverUSer.getEmail());
        binding.imageProfile.setImageBitmap(getBitmapFromEnCodedString(receiverUSer.getProfileImage()));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @SuppressLint("NotifyDataSetChanged")
    private  void getImages(){
        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_STORIES)
                .document(receiverUSer.getUid())
                .collection(Constants.KEY_COLLECTION_STATUSES)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        Log.d("GET_IMAGES" , snap.getId() + " => " + snap.getData());
                        images.add(snap.getString("imageUrl"));
                    }
                    adapter.notifyDataSetChanged();

                });


    }

//    task->
//    {
////                    if (task. && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
////                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
////                        conversionId = documentSnapshot.getId();
////                        countMessage = Integer.valueOf(Objects.requireNonNull(documentSnapshot.getString(Constants.KEY_COUNT_NUMBER_OF_MESSAGE_SEEN)));
////                    }
//    }

    private Bitmap getBitmapFromEnCodedString(String enCodedImage) {
        byte[] bytes = Base64.decode(enCodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}