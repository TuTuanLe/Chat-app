package com.tutuanle.chatapp.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.google.android.material.internal.TextWatcherAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.adapters.SearchAdapter;
import com.tutuanle.chatapp.adapters.Users_Adapter;
import com.tutuanle.chatapp.databinding.ActivitySearchBinding;
import com.tutuanle.chatapp.interfaces.UserListener;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements UserListener {

    ActivitySearchBinding binding;
    PreferenceManager preferenceManager;
    SearchAdapter searchAdapter;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
        setListenerBinding();
        getUSer();
    }

    private void initData() {
        preferenceManager = new PreferenceManager(getApplicationContext());
    }

    private void setListenerBinding() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    binding.removeText.setVisibility(View.VISIBLE);
                    searchFilter(binding.search.getText().toString());
                } else {
                    searchFilter(binding.search.getText().toString());
                    binding.removeText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.removeText.setOnClickListener(v -> binding.search.setText(null));
    }

    @SuppressLint("SetTextI18n")
    private void searchFilter(String name) {
        List<User> temp = new ArrayList<>();
        if (name.length() == 0) {
            temp.addAll(users);
        } else {

            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getName().toLowerCase().contains(name.toLowerCase())) {
                    temp.add(users.get(i));
                }
            }
        }

        if(temp.size() == 0){
            binding.layoutNotFound.setVisibility(View.VISIBLE);
            binding.txtNotFound.setText("No matches were found for " + name + " .Try checking for typos or using complete words. ");
        }else{
            binding.layoutNotFound.setVisibility(View.GONE);
        }
        searchAdapter = new SearchAdapter(temp, SearchActivity.this);
        binding.userRecyclerView.setAdapter(searchAdapter);
        binding.userRecyclerView.setVisibility(View.VISIBLE);


    }


    private void getUSer() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.setName(queryDocumentSnapshot.getString(Constants.KEY_NAME));
                            user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_EMAIL));
                            user.setProfileImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE));
                            user.setToken(queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN));
                            user.setUid(queryDocumentSnapshot.getId());
                            user.setAvailability(queryDocumentSnapshot.getLong(Constants.KEY_AVAILABILITY));
                            users.add(user);

                        }
                        if (users.size() > 0) {
                            searchAdapter = new SearchAdapter(users, SearchActivity.this);
                            binding.userRecyclerView.setAdapter(searchAdapter);
                            binding.userRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showToast("Error");
                        }
                    }
                });
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void openDialogCenter(User user) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_info);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.findViewById(R.id.icon_close).setOnClickListener(v -> dialog.dismiss());
        dialog.setCancelable(true);
        RoundedImageView roundedImageView = (RoundedImageView) dialog.findViewById(R.id.imgAvatar);
        roundedImageView.setImageBitmap(getBitmapFromEnCodedString(user.getProfileImage()));
        TextView username =  dialog.findViewById(R.id.username);
        username.setText(user.getName());
        dialog.findViewById(R.id.imageChat).setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), ChatScreenActivity.class);
            intent.putExtra(Constants.KEY_USER, user);
            startActivity(intent);
            dialog.dismiss();
        });
        dialog.show();
    }



    private Bitmap getBitmapFromEnCodedString(String enCodedImage) {
        byte[] bytes = Base64.decode(enCodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onUserClicked(User user) {
        openDialogCenter(user);
    }

    @Override
    public void initialVideoMeeting(User user) {

    }

    @Override
    public void initialAudioMeeting(User user) {

    }
}