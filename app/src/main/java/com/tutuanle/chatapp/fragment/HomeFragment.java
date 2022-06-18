package com.tutuanle.chatapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.RecyclerView;


import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.firestore.DocumentChange;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.activities.ChatScreenActivity;
import com.tutuanle.chatapp.activities.GroupChatActivity;
import com.tutuanle.chatapp.activities.MainScreenActivity;
import com.tutuanle.chatapp.activities.ProfileActivity;
import com.tutuanle.chatapp.activities.SearchActivity;
import com.tutuanle.chatapp.adapters.HomeFriendAdapter;
import com.tutuanle.chatapp.adapters.TopStatusAdapter;

import com.tutuanle.chatapp.models.ChatMessage;
import com.tutuanle.chatapp.models.Status;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.models.UserStatus;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class HomeFragment extends Fragment {


    private View view;
    private MainScreenActivity mainScreenActivity;
    private PreferenceManager preferenceManager;
    private ArrayList<UserStatus> userStatuses;
    private TopStatusAdapter statusAdapter;
    private HomeFriendAdapter homeFriendAdapter;
    private FirebaseFirestore database;
    private List<ChatMessage> listFriends;


    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        mainScreenActivity = (MainScreenActivity) getActivity();
        assert mainScreenActivity != null;
        preferenceManager = mainScreenActivity.preferenceManager;

        initialData();
        getUserStatus();
        initFriend();
        listenListFriend();
        setListener();
        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initialData() {

        TextView temp = view.findViewById(R.id.nameTextView);
        temp.setText(mainScreenActivity.getTextName());
        RoundedImageView image = view.findViewById(R.id.imageProfile);
        image.setImageBitmap(mainScreenActivity.getBitmap());
        image.setOnClickListener(v -> {
            User usermod = new User();
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            usermod.setName(mainScreenActivity.preferenceManager.getString(Constants.KEY_NAME));
            usermod.setUid(mainScreenActivity.preferenceManager.getString(Constants.KEY_USER_ID));
            usermod.setProfileImage(mainScreenActivity.preferenceManager.getString(Constants.KEY_IMAGE));
            usermod.setPhoneNumber(mainScreenActivity.preferenceManager.getString(Constants.KEY_NUMBER_PHONE));
            usermod.setPassword(mainScreenActivity.preferenceManager.getString(Constants.KEY_PASSWORD));
            usermod.setEmail(mainScreenActivity.preferenceManager.getString(Constants.KEY_EMAIL));
            intent.putExtra(Constants.KEY_USER, usermod);
            startActivity(intent);
        });



    }

    private void initFriend() {
        listFriends = new ArrayList<>();
        homeFriendAdapter = new HomeFriendAdapter(listFriends, mainScreenActivity);
        RecyclerView temp = view.findViewById(R.id.userRecyclerView);
        temp.setAdapter(homeFriendAdapter);
        database = FirebaseFirestore.getInstance();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void getUserStatus() {
        userStatuses = new ArrayList<>();

        statusAdapter = new TopStatusAdapter(mainScreenActivity, userStatuses);
        RecyclerView temp = view.findViewById(R.id.statusList);

        temp.setAdapter(statusAdapter);



        ArrayList<Status> statuses = new ArrayList<>();


        statuses.add(new Status(
                "https://firebasestorage.googleapis.com/v0/b/messagingchatapp.appspot.com/o/status%2F1653551053028?alt=media&token=41ff5595-592e-466f-9ebc-b0a3d3c64a30",
                1648545148
        ));
        statuses.add(new Status(
                "https://firebasestorage.googleapis.com/v0/b/messagingchatapp.appspot.com/o/status%2F1653805717721?alt=media&token=be94c980-afc6-43cd-9a0d-2e7eb0bd3668",
                164853774
        ));


        ArrayList<Status> statuses1 = new ArrayList<>();


        statuses1.add(new Status(
                "https://firebasestorage.googleapis.com/v0/b/messagingchatapp.appspot.com/o/status%2F1653805717721?alt=media&token=be94c980-afc6-43cd-9a0d-2e7eb0bd3668",
                164845148
        ));
        statuses1.add(new Status(
                "https://www.w3schools.com/w3images/fjords.jpg",
                164853774
        ));
        statuses1.add(new Status(
                "https://firebasestorage.googleapis.com/v0/b/messagingchatapp.appspot.com/o/status%2F1654225594003?alt=media&token=f835e910-fda5-477c-a980-b1233e29ef8d",
                164853774
        ));

        ArrayList<Status> statuses2 = new ArrayList<>();


        statuses2.add(new Status(
                "https://www.perma-horti.com/wp-content/uploads/2019/02/image-2.jpg",
                164845148
        ));

        statuses2.add(new Status(
                "https://firebasestorage.googleapis.com/v0/b/messagingchatapp.appspot.com/o/status%2F1655178746355?alt=media&token=651719a3-5afa-4635-bd0c-6c71bcdf96ea",
                164847148
        ));


        ArrayList<Status> statuses3 = new ArrayList<>();


        statuses3.add(new Status(
                "https://firebasestorage.googleapis.com/v0/b/messagingchatapp.appspot.com/o/status%2F1654414233643?alt=media&token=874aaad8-dcc4-4f34-acd6-22a76bba046c",
                164845148
        ));

        statuses3.add(new Status(
                "https://firebasestorage.googleapis.com/v0/b/messagingchatapp.appspot.com/o/status%2F1655181861286?alt=media&token=bf581145-7e2a-470f-9dd3-a014346330d1",
                164847148
        ));





        userStatuses.add(new UserStatus(
                "Tuan Anh",
                "https://firebasestorage.googleapis.com/v0/b/messagingchatapp.appspot.com/o/status%2F1653805717721?alt=media&token=be94c980-afc6-43cd-9a0d-2e7eb0bd3668",
                164853810,
                statuses
        ));
        userStatuses.add(new UserStatus(
                "TuTuanLe",
                "https://www.w3schools.com/w3images/fjords.jpg",
                174853810,
                statuses1
        ));
        userStatuses.add(new UserStatus(
                "TAnh",
                "https://firebasestorage.googleapis.com/v0/b/messagingchatapp.appspot.com/o/status%2F1654223433090?alt=media&token=e3faa34c-c756-4298-aee9-0d9b18a8c679",
                184853810,
                statuses2
        ));

        userStatuses.add(new UserStatus(
                "Phuong",
                "https://firebasestorage.googleapis.com/v0/b/messagingchatapp.appspot.com/o/status%2F1654223433090?alt=media&token=e3faa34c-c756-4298-aee9-0d9b18a8c679",
                184854810,
                statuses3
        ));


        statusAdapter.notifyDataSetChanged();
    }

    private void listenListFriend() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }


    private String getReadableDatetime(Date date) {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);
    }

    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {

            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {

                    String senderID = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMessageId(documentChange.getDocument().getId());
                    chatMessage.setSenderId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                    chatMessage.setReceiverId(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));

                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderID)) {
                        chatMessage.setConversionImage(documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE));
                        chatMessage.setConversionName(documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME));
                        chatMessage.setConversionId(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
//                        chatMessage.setCountMessageSeen("0");

                    } else {
                        chatMessage.setConversionImage(documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE));
                        chatMessage.setConversionName(documentChange.getDocument().getString(Constants.KEY_SENDER_NAME));
                        chatMessage.setConversionId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
//                        chatMessage.setCountMessageSeen(documentChange.getDocument().getString(Constants.KEY_COUNT_NUMBER_OF_MESSAGE_SEEN));
                    }

                    chatMessage.setIsActive(documentChange.getDocument().getString(Constants.KEY_IS_ACTIVE));

                    if(!chatMessage.getIsActive().equals(preferenceManager.getString(Constants.KEY_USER_ID))){
                        chatMessage.setCountMessageSeen(documentChange.getDocument().getString(Constants.KEY_COUNT_NUMBER_OF_MESSAGE_SEEN));
                    }
                    else{
                        chatMessage.setCountMessageSeen("0");
                    }

                    chatMessage.setMessage(documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                    chatMessage.setDateTime(getReadableDatetime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP)));
                    chatMessage.dataObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);

                    listFriends.add(chatMessage);
                    Collections.sort(listFriends, (x, y) -> y.dataObject.compareTo(x.dataObject));
                    homeFriendAdapter.notifyDataSetChanged();

                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    String senderID = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverID = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    for (int i = 0; i < listFriends.size(); i++) {
                        if (listFriends.get(i).getSenderId().equals(senderID) && listFriends.get(i).getReceiverId().equals(receiverID)) {
                            listFriends.get(i).setIsActive(documentChange.getDocument().getString(Constants.KEY_IS_ACTIVE));
                            listFriends.get(i).setMessage(documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                            listFriends.get(i).setDateTime(getReadableDatetime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP)));
                            listFriends.get(i).dataObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            if(!listFriends.get(i).getIsActive().equals(preferenceManager.getString(Constants.KEY_USER_ID))){
                                listFriends.get(i).setCountMessageSeen(documentChange.getDocument().getString(Constants.KEY_COUNT_NUMBER_OF_MESSAGE_SEEN));
                            }
                            else{
                                listFriends.get(i).setCountMessageSeen("0");
                            }

                            break;
                        }
                    }
                    Collections.sort(listFriends, (x, y) -> y.dataObject.compareTo(x.dataObject));
                    homeFriendAdapter.notifyDataSetChanged();
                }


                RecyclerView temp = view.findViewById(R.id.userRecyclerView);
                temp.setAdapter(homeFriendAdapter);
                temp.smoothScrollToPosition(0);
                temp.setVisibility(View.VISIBLE);
                ProgressBar progressBar = view.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);

            }

        }


    };


    private void setListener() {
        view.findViewById(R.id.search_friend).setOnClickListener(v ->
                startActivity(new Intent(mainScreenActivity, SearchActivity.class)));
    }





    @SuppressLint("NotifyDataSetChanged")
    void getStatuesListener() {

        for (int i = 0; i < userStatuses.size(); i++) {
            int b = i;
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
                                    statuses.add(statusTemp);
                                }
                            }
                        }
                    });


        }


    }


}