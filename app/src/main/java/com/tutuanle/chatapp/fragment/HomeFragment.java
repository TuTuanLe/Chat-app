package com.tutuanle.chatapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.firebase.firestore.DocumentChange;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

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

        view.findViewById(R.id.groupChat).setOnClickListener(v->{
            Intent intent = new Intent(mainScreenActivity, GroupChatActivity.class);
            User user = new User();
            user.setName("group1");
            user.setUid("tutuanletkpt");
            user.setProfileImage("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");
            user.setPhoneNumber("12321343345345");
            user.setPassword("123123123123123");
            user.setEmail("tutuanletkpt@gmail.com");
            intent.putExtra(Constants.KEY_USER, user);
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
                "https://firebasestorage.googleapis.com/v0/b/chatsapp-4b8d6.appspot.com/o/status%2F1648451488181?alt=media&token=9e1f4eb7-8825-43c2-8220-106c92409620",
                164845148
        ));
        statuses.add(new Status(
                "https://firebasestorage.googleapis.com/v0/b/chatsapp-4b8d6.appspot.com/o/status%2F1648451488181?alt=media&token=9e1f4eb7-8825-43c2-8220-106c92409620",
                164853774
        ));


        ArrayList<Status> statuses1 = new ArrayList<>();


        statuses1.add(new Status(
                "hhttps://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
                164845148
        ));
        statuses1.add(new Status(
                "https://www.w3schools.com/w3images/fjords.jpg",
                164853774
        ));
        statuses1.add(new Status(
                "https://st.depositphotos.com/1006706/2671/i/600/depositphotos_26715369-stock-photo-which-way-to-choose-3d.jpg",
                164853774
        ));

        ArrayList<Status> statuses2 = new ArrayList<>();


        statuses2.add(new Status(
                "https://www.perma-horti.com/wp-content/uploads/2019/02/image-2.jpg",
                164845148
        ));


        userStatuses.add(new UserStatus(
                "Tuan Anh",
                "https://firebasestorage.googleapis.com/v0/b/chatsapp-4b8d6.appspot.com/o/status%2F1648537741702?alt=media&token=685fb7d8-a05d-429d-9834-1db74b41ff0e",
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
                "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
                184853810,
                statuses2
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
}