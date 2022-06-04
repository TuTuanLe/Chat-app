package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tutuanle.chatapp.adapters.InformationAdapter;
import com.tutuanle.chatapp.databinding.ActivityInfomationBinding;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;
import java.util.ArrayList;
import java.util.List;


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

    @SuppressLint({"NotifyDataSetChanged", "LogNotTimber"})
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



    private Bitmap getBitmapFromEnCodedString(String enCodedImage) {
        byte[] bytes = Base64.decode(enCodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}