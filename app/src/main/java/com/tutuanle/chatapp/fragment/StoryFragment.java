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


    class Sortbyroll implements Comparator<UserStatus> {
        public int compare(UserStatus a, UserStatus b) {
            return (int) b.getLastUpdated() - (int) a.getLastUpdated();
        }
    }

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
        temp.setAdapter(storyAdapter);
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

    synchronized void getOnStoriesListener() {
        database.collection(Constants.KEY_COLLECTION_STORIES)
                .addSnapshotListener(eventListener);
    }

    synchronized void getStatuesListener() {

        for (int i = 0; i < userStatuses.size(); i++) {
            int b = i;
            Log.d("TAG_STATUS", ": " + userStatuses.get(i).getStatusUid());

            database.collection(Constants.KEY_COLLECTION_STORIES)
                    .document(userStatuses.get(i).getStatusUid())
                    .collection(Constants.KEY_COLLECTION_STATUSES)
                    .addSnapshotListener((valueStatuses, errorStatuses) -> {
                        if (errorStatuses != null) {
                            return;
                        }
                        if (valueStatuses != null) {
                            ArrayList<Status> statuses = new ArrayList<>();
                            for (DocumentChange documentChangeStatus : valueStatuses.getDocumentChanges()) {
                                if (documentChangeStatus.getType() == DocumentChange.Type.ADDED) {
                                    Status statusTemp = new Status();
                                    statusTemp.setImageUrl(documentChangeStatus.getDocument().getString("imageUrl"));
                                    statusTemp.setTimeStamp(documentChangeStatus.getDocument().getLong("timeStamp"));
                                    Log.d("TAG_STATUS", ": " + statusTemp.getImageUrl());
                                    statuses.add(statusTemp);
                                }
                            }

                            userStatuses.get(b).setStatuses(statuses);
                            Collections.sort(userStatuses, new Sortbyroll());
                            if (userStatuses.get(userStatuses.size() - 1).getStatuses() != null) {
                                storyAdapter.notifyDataSetChanged();
                            }
                        }
                    });


        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {

            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    UserStatus status = new UserStatus();
                    status.setStatusUid(documentChange.getDocument().getId());
                    status.setName(documentChange.getDocument().getString(Constants.KEY_NAME));
                    status.setProfileImage(documentChange.getDocument().getString(Constants.KEY_IMAGE));
                    status.setLastUpdated(documentChange.getDocument().getLong("lastUpdate"));
                    userStatuses.add(status);
                }
            }
            Collections.sort(userStatuses, new Sortbyroll());
            getStatuesListener();


        }
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
//                        userStatuses.add(userStatus);
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