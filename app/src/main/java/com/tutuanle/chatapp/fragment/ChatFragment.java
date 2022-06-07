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
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.activities.ChatScreenActivity;
import com.tutuanle.chatapp.activities.CreateGroupActivity;
import com.tutuanle.chatapp.activities.GroupChatActivity;
import com.tutuanle.chatapp.activities.InformationActivity;
import com.tutuanle.chatapp.activities.MainScreenActivity;
import com.tutuanle.chatapp.activities.ProfileActivity;
import com.tutuanle.chatapp.activities.SearchActivity;
import com.tutuanle.chatapp.adapters.RequestAdapter;
import com.tutuanle.chatapp.adapters.UserGroupAdapter;
import com.tutuanle.chatapp.adapters.Users_Adapter;
import com.tutuanle.chatapp.interfaces.RequestListener;
import com.tutuanle.chatapp.interfaces.UsersListener;
import com.tutuanle.chatapp.models.RequestFriend;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class ChatFragment extends Fragment implements RequestListener, UsersListener {

    private View view;

    private MainScreenActivity mainScreenActivity;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore firebaseFirestore;
    private List<User> users;
    private List<RequestFriend> requestFriends;
    private List<User> listFriend;
    private String uidReceiverFriend;
    private UserGroupAdapter userGroupAdapter;
    private List<User> userGroup;

    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        mainScreenActivity = (MainScreenActivity) getActivity();
        assert mainScreenActivity != null;
        preferenceManager = mainScreenActivity.preferenceManager;

        initialData();
        getUSer();
        setListener();
        getRequestFriend();
        initGroupChat();
        return view;
    }

    private void setListener() {
        view.findViewById(R.id.search_friend).setOnClickListener(v ->
                startActivity(new Intent(mainScreenActivity, SearchActivity.class)));
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

                    }
                });


    }

    private void getRequestFriend() {

        requestFriends = new ArrayList<>();
        listFriend = new ArrayList<>();
        new Handler().postDelayed(() -> {
            try {
                initRequestFriend();
                initFriend();
                for (int i = 0; i < users.size(); i++) {
                    initAcceptFriend(users.get(i).getUid());
                }

            } catch (Exception e) {
                showErrorMessage();
            }

        }, 1000);

    }

    private void initRequestFriend() {
        firebaseFirestore
                .collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_STATUS, 0)
                .addSnapshotListener(eventListener);
    }

    private void initAcceptFriend(String receiver) {
        firebaseFirestore
                .collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiver)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_STATUS, 0)
                .addSnapshotListener(eventAcceptListener);
    }

    private void initFriend() {
        firebaseFirestore
                .collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_STATUS, 1)
                .addSnapshotListener(eventFriendListener);
    }

    private void initGroupChat(){
        firebaseFirestore
                .collection(Constants.KEY_COLLECTION_USER_GROUP)
                .addSnapshotListener(eventGroupListener);
    }
    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private final EventListener<QuerySnapshot> eventGroupListener = (value, error) -> {

        if (error != null) {
            return;
        }
        if (value != null) {

            for (DocumentChange documentChange : value.getDocumentChanges()) {

                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    int check = 0;
                    if(documentChange.getDocument().getString(Constants.KEY_USER_ADMIN_GROUP).equals(preferenceManager.getString(Constants.KEY_USER_ID)) ){
                        check = 1;
                    }else{
                        List<String> userTemp = new ArrayList<>();
                        userTemp.addAll((ArrayList<String>) documentChange.getDocument().get("memberUid"));
                        try{
                            for(int i =0 ; i< userTemp.size(); i++){
                                if(userTemp.get(i).toString().equals(preferenceManager.getString(Constants.KEY_USER_ID))){
                                    check  =1;
                                    break;
                                }
                            }
                        }catch (Exception e){
                            showErrorMessage();
                        }

                    }
                    if(check == 1){
                        User user = new User();
                        user.setUid(documentChange.getDocument().getId());
                        user.setProfileImage(documentChange.getDocument().getString(Constants.KEY_IMAGE));
                        user.setName(documentChange.getDocument().getString(Constants.KEY_NAME));
                        userGroup.add(user);
                    }

                }
                try{
                    RecyclerView temp = view.findViewById(R.id.groupRecyclerView);
                    temp.setAdapter(userGroupAdapter);
                    userGroupAdapter.notifyDataSetChanged();
                    temp.setVisibility(View.VISIBLE);

//                    TextView txtExist = view.findViewById(R.id.txtNotExits);
//                    if(userGroup.size() > 0){
//                        txtExist.setVisibility(View.GONE);
//                    }else{
//                        txtExist.setVisibility(View.VISIBLE);
//                    }
                }
                catch (Exception e){
                    showErrorMessage();
                }

            }
            TextView txtExist = view.findViewById(R.id.txtNotExits);
            if(userGroup.size() > 0){
                txtExist.setVisibility(View.GONE);
            }else{
                txtExist.setVisibility(View.VISIBLE);
            }

        }
    };





    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private final EventListener<QuerySnapshot> eventFriendListener = (value, error) -> {

        if (error != null) {
            return;
        }
        if (value != null) {

            for (DocumentChange documentChange : value.getDocumentChanges()) {

                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    int index = findUser(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    if (index > -1) {
                        listFriend.add(users.get(index));
                    }


                } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                    int index = findUser(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    if (index > -1) {
                        listFriend.remove(index);
                    }

                }
                try{

                        Users_Adapter users_adapter = new Users_Adapter(listFriend, mainScreenActivity);
                        RecyclerView temp = view.findViewById(R.id.userRecyclerView);
                        temp.setAdapter(users_adapter);
                        temp.setVisibility(View.VISIBLE);

                }
                catch (Exception e){
                    showErrorMessage();
                }

            }


        }
    };


    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {

        if (error != null) {
            return;
        }
        if (value != null) {

            for (DocumentChange documentChange : value.getDocumentChanges()) {

                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                   try {
                       int index = findUser(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                       requestFriends.add(new RequestFriend(
                               documentChange.getDocument().getId(),
                               users.get(index).getProfileImage(),
                               users.get(index).getName(),
                               "CANCEL",
                               documentChange.getDocument().getString(Constants.KEY_SENDER_ID),
                               documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID)
                       ));
                       RequestAdapter requestAdapter = new RequestAdapter(requestFriends, this);
                       RecyclerView temp = view.findViewById(R.id.requestRecyclerView);
                       temp.setAdapter(requestAdapter);
                   }catch (Exception e){
                       showErrorMessage();
                   }


                } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                    int index = findRequestFriend(documentChange.getDocument().getId());
                    requestFriends.remove(index);
                    RequestAdapter requestAdapter = new RequestAdapter(requestFriends, this);
                    RecyclerView temp = view.findViewById(R.id.requestRecyclerView);
                    temp.setAdapter(requestAdapter);
                }

            }
            TextView txtExist = view.findViewById(R.id.txtNotExitsRequest);
            if(requestFriends.size() > 0){
                txtExist.setVisibility(View.GONE);
            }else{
                txtExist.setVisibility(View.VISIBLE);
            }
        }
    };


    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private final EventListener<QuerySnapshot> eventAcceptListener = (value, error) -> {

        if (error != null) {
            return;
        }
        if (value != null) {

            for (DocumentChange documentChange : value.getDocumentChanges()) {

                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    int index = findUser(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                    requestFriends.add(new RequestFriend(
                            documentChange.getDocument().getId(),
                            users.get(index).getProfileImage(),
                            users.get(index).getName(),
                            "ACCEPT",
                            documentChange.getDocument().getString(Constants.KEY_SENDER_ID),
                            documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID)
                    ));

                    RequestAdapter requestAdapter = new RequestAdapter(requestFriends, this);
                    RecyclerView temp = view.findViewById(R.id.requestRecyclerView);
                    temp.setAdapter(requestAdapter);
                    break;
                } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                    int index = findRequestFriend(documentChange.getDocument().getId());
                    requestFriends.remove(index);
                    RequestAdapter requestAdapter = new RequestAdapter(requestFriends, this);
                    RecyclerView temp = view.findViewById(R.id.requestRecyclerView);
                    temp.setAdapter(requestAdapter);
                    break;
                }

            }
            TextView txtExist = view.findViewById(R.id.txtNotExitsRequest);
            if(requestFriends.size() > 0){
                txtExist.setVisibility(View.GONE);
            }else{
                txtExist.setVisibility(View.VISIBLE);
            }

        }
    };


    private int findUser(String uid) {
        try {
            for (int i = 0; i < users.size(); i++)
                if (users.get(i).getUid().equals(uid))
                    return i;
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }




    private int findRequestFriend(String uid) {
        for (int i = 0; i < requestFriends.size(); i++)
            if (requestFriends.get(i).getRequestUid().equals(uid))
                return i;
        return -1;
    }

    private void initialData() {
        userGroup = new ArrayList<>();
        userGroupAdapter = new UserGroupAdapter(userGroup, this);
        TextView nameTextView = view.findViewById(R.id.nameTextView);
        nameTextView.setText(mainScreenActivity.getTextName());
        RoundedImageView image = view.findViewById(R.id.imageProfile);
        image.setImageBitmap(mainScreenActivity.getBitmap());
        firebaseFirestore = FirebaseFirestore.getInstance();
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
        view.setOnClickListener(v -> startActivity(new Intent(mainScreenActivity, CreateGroupActivity.class)));
    }

    private void loading(Boolean isLoading) {
        TashieLoader temp = view.findViewById(R.id.progressBar);
        if (isLoading) {
            temp.setVisibility(View.VISIBLE);
        } else {
            temp.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void showErrorMessage() {
        TextView temp = view.findViewById(R.id.textErrorMessage);
        temp.setText("Not exist");
        temp.setVisibility(View.VISIBLE);
    }

    private void deleteRequestFriend(String Uid) {
        firebaseFirestore.collection(Constants.KEY_COLLECTION_FRIENDS).document(Uid).delete();
    }

    private void createAcceptFriend(String sender, String receiver) {
        HashMap<String, Object> request = new HashMap<>();
        request.put(Constants.KEY_SENDER_ID, sender);
        request.put(Constants.KEY_RECEIVER_ID, receiver);
        request.put(Constants.KEY_STATUS, (long) 1);

        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_FRIENDS).add(request);

        checkForConversionRemotely(receiver, sender);

        new Handler().postDelayed(() ->
                FirebaseFirestore
                        .getInstance()
                        .collection(Constants.KEY_COLLECTION_FRIENDS)
                        .document(uidReceiverFriend)
                        .update(Constants.KEY_STATUS, 1), 1000);

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

    @Override
    public void onAcceptFiend(String receiver) {
        createAcceptFriend(preferenceManager.getString(Constants.KEY_USER_ID), receiver);
    }

    @Override
    public void onCancelRequestFriend(String uid) {
        deleteRequestFriend(uid);
    }

    @Override
    public void onShowUser(String uid) {
        FirebaseFirestore.getInstance()
                .collection(Constants.KEY_COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(v ->
                        {
                            User user = new User();
                            user.setUid(uid);
                            user.setName(v.getString(Constants.KEY_NAME));
                            user.setEmail(v.getString(Constants.KEY_EMAIL));
                            user.setProfileImage(v.getString(Constants.KEY_IMAGE));
                            user.setPhoneNumber(v.getString(Constants.KEY_NUMBER_PHONE));

                            Intent intent = new Intent(mainScreenActivity, InformationActivity.class);
                            intent.putExtra(Constants.KEY_USER, user);
                            startActivity(intent);
                        }
                );

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onUsersClicked(User user) {
        Intent intent = new Intent(mainScreenActivity , GroupChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}