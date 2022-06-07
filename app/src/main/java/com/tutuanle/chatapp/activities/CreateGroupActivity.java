package com.tutuanle.chatapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.adapters.GroupAdapter;
import com.tutuanle.chatapp.adapters.UserCheckedAdapter;
import com.tutuanle.chatapp.adapters.UserGroupAdapter;
import com.tutuanle.chatapp.databinding.ActivityCreateGroupBinding;
import com.tutuanle.chatapp.interfaces.GroupListener;
import com.tutuanle.chatapp.interfaces.UserListener;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity implements UserListener, GroupListener {

    ActivityCreateGroupBinding binding;
    PreferenceManager preferenceManager;
    GroupAdapter groupAdapter;
    UserCheckedAdapter userCheckedAdapter;
    private List<User> users;
    private int status = -1;
    private FirebaseFirestore firebaseFirestore;
    private TextView temp;
    private String uidFriend;
    private String uidReceiverFriend;
    private int checkFriends = 0;
    private int checkReceiverFriends = 0;
    private List<User> userChecked;
    private String encodedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
        setListenerBinding();
        getUSer();
    }

    private void initData() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        firebaseFirestore = FirebaseFirestore.getInstance();
        userChecked = new ArrayList<>();

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
//                    binding.removeText.setVisibility(View.VISIBLE);
                    searchFilter(binding.search.getText().toString());
                } else {
                    searchFilter(binding.search.getText().toString());
//                    binding.removeText.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
//        binding.removeText.setOnClickListener(v -> binding.search.setText(null));

        binding.nextStep.setOnClickListener(v -> {
            binding.cardHeader.setVisibility(View.GONE);
            binding.scrollable.setVisibility(View.GONE);
            binding.cardViewGroup.setVisibility(View.VISIBLE);
            binding.cardViewHeaderTwo.setVisibility(View.VISIBLE);
            setListUserChecked();
        });

        binding.goBackSearch.setOnClickListener(v -> {
            binding.cardHeader.setVisibility(View.VISIBLE);
            binding.scrollable.setVisibility(View.VISIBLE);
            binding.cardViewGroup.setVisibility(View.GONE);
            binding.cardViewHeaderTwo.setVisibility(View.GONE);
//            userChecked.clear();
        });

        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

        binding.btnComplete.setOnClickListener(v -> {
            HashMap<String, Object> group = new HashMap<>();
            group.put(Constants.KEY_NAME, binding.inputNameGroup.getText().toString());
            group.put(Constants.KEY_IMAGE, encodedImage);
            group.put(Constants.KEY_USER_ADMIN_GROUP, preferenceManager.getString(Constants.KEY_USER_ID));
            group.put("memberUid", userChecked);


            FirebaseFirestore.getInstance()
                    .collection(Constants.KEY_COLLECTION_USER_GROUP)
                    .add(group);
            onBackPressed();
        });
    }


    private void setListUserChecked() {

        userCheckedAdapter = new UserCheckedAdapter(userChecked);
        binding.userRecyclerViewChecked.setAdapter(userCheckedAdapter);
        try {
            binding.textParticipants.setText("Participants: " + userChecked.size());
        } catch (Exception e) {
            binding.textParticipants.setText("Participants: 0");
        }

    }

    @SuppressLint("SetTextI18n")
    private void searchFilter(String name) {
        List<User> tempLs = new ArrayList<>();
        if (name.length() == 0) {
            tempLs.addAll(users);
        } else {

            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getName().toLowerCase().contains(name.toLowerCase())) {
                    tempLs.add(users.get(i));
                }
            }
        }

        if (tempLs.size() == 0) {
            binding.layoutNotFound.setVisibility(View.VISIBLE);
            binding.txtNotFound.setText("No matches were found for " + name + " .Try checking for typos or using complete words. ");
        } else {
            binding.layoutNotFound.setVisibility(View.GONE);
        }
        groupAdapter = new GroupAdapter(tempLs, CreateGroupActivity.this, CreateGroupActivity.this);
        binding.userRecyclerView.setAdapter(groupAdapter);
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
                            groupAdapter = new GroupAdapter(users, CreateGroupActivity.this, CreateGroupActivity.this);
                            binding.userRecyclerView.setAdapter(groupAdapter);
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

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
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
        TextView username = dialog.findViewById(R.id.username);
        username.setText(user.getName());

        temp = (TextView) dialog.findViewById(R.id.textRequest);
        initFriend(preferenceManager.getString(Constants.KEY_USER_ID), user.getUid());

        dialog.findViewById(R.id.imageChat).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ChatScreenActivity.class);
            intent.putExtra(Constants.KEY_USER, user);
            startActivity(intent);
            dialog.dismiss();
        });

        dialog.findViewById(R.id.imageCall).setOnClickListener(v -> {
            initialAudioMeeting(user);
            dialog.dismiss();
        });

        dialog.findViewById(R.id.imageVideo).setOnClickListener(v -> {
            initialVideoMeeting(user);
            dialog.dismiss();
        });

        dialog.findViewById(R.id.viewProfile).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), InformationActivity.class);
            intent.putExtra(Constants.KEY_USER, user);
            startActivity(intent);
        });

        dialog.findViewById(R.id.sendRequest).setOnClickListener(v -> {
            if (temp.getText().equals("REQUEST")) {
                createRequestFriend(preferenceManager.getString(Constants.KEY_USER_ID), user.getUid());
                temp.setText("CANCEL");
                temp.setTextColor(getResources().getColor(R.color.icon_background));
            } else if (temp.getText().equals("CANCEL")) {
                deleteRequestFriend(uidFriend);
                temp.setText("REQUEST");
                temp.setTextColor(getResources().getColor(R.color.blue));
            } else if (temp.getText().equals("ACCEPT")) {
                createAcceptFriend(preferenceManager.getString(Constants.KEY_USER_ID), user.getUid());
                temp.setText("UNFRIEND");
                temp.setTextColor(getResources().getColor(R.color.icon_background));
            } else if (temp.getText().equals("UNFRIEND")) {
                deleteUnfriendFriend(uidFriend, preferenceManager.getString(Constants.KEY_USER_ID), user.getUid());
                temp.setText("ACCEPT");
                temp.setTextColor(getResources().getColor(R.color.blue));
            }
        });


        dialog.show();
    }


    private Bitmap getBitmapFromEnCodedString(String enCodedImage) {
        byte[] bytes = Base64.decode(enCodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    private void createRequestFriend(String sender, String receiver) {
        HashMap<String, Object> request = new HashMap<>();
        request.put(Constants.KEY_SENDER_ID, sender);
        request.put(Constants.KEY_RECEIVER_ID, receiver);
        request.put(Constants.KEY_STATUS, (long) 0);

        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_FRIENDS)
                .add(request)
                .addOnSuccessListener(value -> {
                    uidFriend = value.getId();
                    showToast("sending request ...");
                });
    }

    private void createAcceptFriend(String sender, String receiver) {
        HashMap<String, Object> request = new HashMap<>();
        request.put(Constants.KEY_SENDER_ID, sender);
        request.put(Constants.KEY_RECEIVER_ID, receiver);
        request.put(Constants.KEY_STATUS, (long) 1);

        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_FRIENDS)
                .add(request)
                .addOnSuccessListener(value -> {
                    uidFriend = value.getId();
                    showToast(" became friends ... ");
                });

        checkForConversionRemotely(receiver, sender);


        new Handler().postDelayed(() -> {
            FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_FRIENDS)
                    .document(uidReceiverFriend)
                    .update(Constants.KEY_STATUS, 1);
        }, 1000);

    }


    private void checkForConversionRemotely(String senderId, String receiverId) {
        FirebaseFirestore.getInstance()
                .collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(getUidOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> getUidOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            uidReceiverFriend = documentSnapshot.getId();
        }
    };


    private void initFriend(String sender, String receiver) {

        firebaseFirestore
                .collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo(Constants.KEY_SENDER_ID, sender)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiver)
                .addSnapshotListener(eventListener);
        firebaseFirestore
                .collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiver)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, sender)
                .addSnapshotListener(eventListener);

    }

    private void deleteRequestFriend(String Uid) {
        firebaseFirestore.collection(Constants.KEY_COLLECTION_FRIENDS).document(Uid).delete();
    }

    private void deleteUnfriendFriend(String Uid, String sender, String receiver) {
        firebaseFirestore.collection(Constants.KEY_COLLECTION_FRIENDS).document(Uid).delete();

        checkForConversionRemotely(receiver, sender);

        new Handler().postDelayed(() -> {
            FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_FRIENDS)
                    .document(uidReceiverFriend)
                    .update(Constants.KEY_STATUS, 0);
        }, 1000);
    }


    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {

        if (error != null) {
            return;
        }
        if (value != null) {

            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getDocument().getString(Constants.KEY_SENDER_ID).equals(preferenceManager.getString(Constants.KEY_USER_ID))) {
                    uidFriend = documentChange.getDocument().getId();
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        if (checkReceiverFriends != 0) {
                            temp.setText("UNFRIEND");
                            temp.setTextColor(getResources().getColor(R.color.icon_background));
                            checkFriends = 1;
                        } else {
                            temp.setText("CANCEL");
                            temp.setTextColor(getResources().getColor(R.color.icon_background));
                            checkFriends = 1;
                        }

                    }
                    if (documentChange.getType() == DocumentChange.Type.REMOVED) {

                        if (checkReceiverFriends != 0) {
                            temp.setText("ACCEPT");
                            temp.setTextColor(getResources().getColor(R.color.blue));
                            checkFriends = 0;
                            checkReceiverFriends = 1;
                        } else {
                            temp.setText("REQUEST");
                            temp.setTextColor(getResources().getColor(R.color.blue));
                            checkFriends = 0;
                            checkReceiverFriends = 0;
                        }

                    }


                } else {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        if (checkFriends != 0) {
                            temp.setText("UNFRIEND");
                            temp.setTextColor(getResources().getColor(R.color.icon_background));
                            checkReceiverFriends = 1;
                            checkFriends = 1;
                        } else {
                            temp.setText("ACCEPT");
                            temp.setTextColor(getResources().getColor(R.color.blue));
                            checkReceiverFriends = 1;
                        }
                    }
                    if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                        if (checkFriends != 0) {
                            temp.setText("CANCEL");
                            temp.setTextColor(getResources().getColor(R.color.icon_background));
                            checkFriends = 1;
                            checkReceiverFriends = 0;
                        } else {
                            temp.setText("REQUEST");
                            temp.setTextColor(getResources().getColor(R.color.blue));
                            checkFriends = 0;
                            checkReceiverFriends = 0;
                        }

                    }
                }
                Log.d("TAG_TEST_FRIEND", "check sender: " + checkFriends + "  | check recevier: " + checkReceiverFriends);
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onUserClicked(User user) {
        openDialogCenter(user);
    }

    @Override
    public void initialVideoMeeting(User user) {
        if (user.getToken() == null || user.getToken().trim().isEmpty()) {
            showToast(user.getName() + " is not available for meeting ...");
        } else {
            showToast(user.getName() + " video call ...");
            Intent intent = new Intent(getApplicationContext(), OutgoingActivity.class);
            intent.putExtra(Constants.KEY_USER, user);
            intent.putExtra("type_call", "video");
            startActivity(intent);
        }
    }

    @Override
    public void initialAudioMeeting(User user) {
        if (user.getToken() == null || user.getToken().trim().isEmpty()) {
            showToast(user.getName() + " is not available for calling ...");
        } else {
            showToast(user.getName() + " call ...");
            Intent intent = new Intent(getApplicationContext(), OutgoingActivity.class);
            intent.putExtra(Constants.KEY_USER, user);
            intent.putExtra("type_call", "audio");
            startActivity(intent);
        }
    }

    @Override
    public void getUserChecked(User user) {
        showToast(user.getName() + " is checked. ");
        userChecked.add(user);
    }


    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
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
                        binding.imageProfile.setImageBitmap(bitmap);
                        binding.layoutImageView.setVisibility(View.GONE);
                        encodedImage = encodeImage(bitmap);


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
    );
}