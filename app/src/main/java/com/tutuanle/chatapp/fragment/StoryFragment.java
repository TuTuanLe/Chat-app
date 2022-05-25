package com.tutuanle.chatapp.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.activities.MainScreenActivity;
import com.tutuanle.chatapp.adapters.StoryAdapter;
import com.tutuanle.chatapp.models.ChatMessage;
import com.tutuanle.chatapp.models.Status;
import com.tutuanle.chatapp.models.UserStatus;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.GridSpacingItemDecoration;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


public class StoryFragment extends Fragment {

    private View view;
    private MainScreenActivity mainScreenActivity;

    private StoryAdapter storyAdapter;
    private PreferenceManager preferenceManager;
    private ArrayList<UserStatus> userStatuses;
    FirebaseFirestore database;
    ProgressDialog dialog;

    public StoryFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_story, container, false);
        mainScreenActivity = (MainScreenActivity) getActivity();
        assert mainScreenActivity != null;
        preferenceManager = mainScreenActivity.preferenceManager;
        getUserStatus();
        initialData();
        setOnClickListener();
        getOnStoriesListener();
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getUserStatus() {
        userStatuses = new ArrayList<>();

        storyAdapter = new StoryAdapter(mainScreenActivity, userStatuses);
        RecyclerView temp = view.findViewById(R.id.statusList);
//        temp.setLayoutManager(new GridLayoutManager(mainScreenActivity, 1));

        temp.setAdapter(storyAdapter);

//        ArrayList<Status> statuses = new ArrayList<>();
//
//
//        statuses.add(new Status(
//                "https://firebasestorage.googleapis.com/v0/b/chatsapp-4b8d6.appspot.com/o/status%2F1648451488181?alt=media&token=9e1f4eb7-8825-43c2-8220-106c92409620",
//                164845148
//        ));
//        statuses.add(new Status(
//                "https://firebasestorage.googleapis.com/v0/b/chatsapp-4b8d6.appspot.com/o/status%2F1648451488181?alt=media&token=9e1f4eb7-8825-43c2-8220-106c92409620",
//                164853774
//        ));
//
//
//        ArrayList<Status> statuses1 = new ArrayList<>();
//
//
//        statuses1.add(new Status(
//                "hhttps://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
//                164845148
//        ));
//        statuses1.add(new Status(
//                "https://www.w3schools.com/w3images/fjords.jpg",
//                164853774
//        ));
//        statuses1.add(new Status(
//                "https://st.depositphotos.com/1006706/2671/i/600/depositphotos_26715369-stock-photo-which-way-to-choose-3d.jpg",
//                164853774
//        ));
//
//        ArrayList<Status> statuses2 = new ArrayList<>();
//
//
//        statuses2.add(new Status(
//                "https://www.perma-horti.com/wp-content/uploads/2019/02/image-2.jpg",
//                164845148
//        ));
//
//
//        userStatuses.add(new UserStatus(
//                "Tuan Anh",
//                "https://firebasestorage.googleapis.com/v0/b/chatsapp-4b8d6.appspot.com/o/status%2F1648537741702?alt=media&token=685fb7d8-a05d-429d-9834-1db74b41ff0e",
//                164853810,
//                statuses
//        ));
//        userStatuses.add(new UserStatus(
//                "TuTuanLe",
//                "https://www.w3schools.com/w3images/fjords.jpg",
//                174853810,
//                statuses1
//        ));
//        userStatuses.add(new UserStatus(
//                "TAnh",
//                "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
//                184853810,
//                statuses2
//        ));
//
//        userStatuses.add(new UserStatus(
//                "Tram Huynh",
//                "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
//                9,
//                statuses2
//        ));
//        userStatuses.add(new UserStatus(
//                "Phuong",
//                "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
//                9,
//                statuses2
//        ));
//        userStatuses.add(new UserStatus(
//                "Nhon",
//                "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
//                9,
//                statuses2
//        ));
//
//
//        storyAdapter.notifyDataSetChanged();
    }


    private void initialData() {

        TextView temp = view.findViewById(R.id.nameTextView);
        temp.setText(mainScreenActivity.getTextName());
        RoundedImageView image = view.findViewById(R.id.imageProfile);
        image.setImageBitmap(mainScreenActivity.getBitmap());
        dialog = new ProgressDialog(mainScreenActivity);
        dialog.setMessage("upLoading Image ...");
        dialog.setCancelable(false);
        database = FirebaseFirestore.getInstance();
    }

    private void setOnClickListener() {
        view.findViewById(R.id.addStoryBook).setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 2);
        });
    }

    public void getOnStoriesListener() {
        database.collection(Constants.KEY_COLLECTION_STORIES)
                .addSnapshotListener(eventListener);
    }

    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {

            for (DocumentChange documentChange : value.getDocumentChanges()) {
//                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                UserStatus status = new UserStatus();
                status.setStatusUid(documentChange.getDocument().getId());
                status.setName(documentChange.getDocument().getString(Constants.KEY_NAME));
                status.setProfileImage(documentChange.getDocument().getString(Constants.KEY_IMAGE));
                status.setLastUpdated(documentChange.getDocument().getLong("lastUpdate"));

                Log.d("TAG_STATUS", ": " + status.getName() + status.getLastUpdated());

                ArrayList<Status> statuses = new ArrayList<>();


                database.collection(Constants.KEY_COLLECTION_STORIES)
                        .document()
                        .collection(Constants.KEY_COLLECTION_STATUSES)
                        .addSnapshotListener((valueStatuses, errorStatuses) -> {
                            if (errorStatuses != null) {
                                return;
                            }
                            if (valueStatuses != null) {
                                for (DocumentChange documentChangeStatus : valueStatuses.getDocumentChanges()) {
                                    if (documentChangeStatus.getType() == DocumentChange.Type.ADDED) {
                                        Status statusTemp = new Status();
                                        statusTemp.setImageUrl(documentChangeStatus.getDocument().getString("imageUrl"));
                                        // statusTemp.setTimeStamp(documentChangeStatus.getDocument().getLong(21312324));
                                        Log.d("TAG_STATUS", ": " + statusTemp.getImageUrl());
                                        statuses.add(statusTemp);
                                    }
                                }
                            }
                        });


                Log.d("TAG_STATUS", String.valueOf(" : " + status.getStatusUid() + " | " + status.getName()));

                status.setStatuses(statuses);
                userStatuses.add(status);

            }
        }
//            storyAdapter.notifyDataSetChanged();
//        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == -1 && data != null) {
            dialog.show();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            Date date = new Date();
            StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");
            reference.putFile(data.getData()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        UserStatus userStatus = new UserStatus();
                        userStatus.setName(preferenceManager.getString(Constants.KEY_NAME));
                        userStatus.setProfileImage(preferenceManager.getString(Constants.KEY_IMAGE));
                        userStatus.setLastUpdated(date.getTime());
                        userStatuses.add(userStatus);
                        HashMap<String, Object> obj = new HashMap<>();
                        obj.put("name", userStatus.getName());
                        obj.put("profileImage", userStatus.getProfileImage());
                        obj.put("lastUpdate", userStatus.getLastUpdated());

                        String imageUrl = uri.toString();
                        Status status = new Status(imageUrl, userStatus.getLastUpdated());

                        database.collection(Constants.KEY_COLLECTION_STORIES)
                                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                                .collection(Constants.KEY_COLLECTION_STATUSES)
                                .add(status);
                        database.collection(Constants.KEY_COLLECTION_STORIES)
                                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                                .set(obj);
                        ;
                        dialog.dismiss();
                    });
                }
            });

        }
    }


}