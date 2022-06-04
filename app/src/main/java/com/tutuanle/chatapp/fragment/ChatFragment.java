package com.tutuanle.chatapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
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
import com.tutuanle.chatapp.activities.InformationActivity;
import com.tutuanle.chatapp.activities.MainScreenActivity;
import com.tutuanle.chatapp.activities.ProfileActivity;
import com.tutuanle.chatapp.activities.SearchActivity;
import com.tutuanle.chatapp.adapters.RequestAdapter;
import com.tutuanle.chatapp.adapters.Users_Adapter;
import com.tutuanle.chatapp.interfaces.RequestListener;
import com.tutuanle.chatapp.models.RequestFriend;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChatFragment extends Fragment implements RequestListener {

    private View view;

    private MainScreenActivity mainScreenActivity;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore firebaseFirestore;
    private List<User> users;
    private List<RequestFriend> requestFriends;
    private List<User> listFriend;
    private String uidReceiverFriend;

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
            try{
                initRequestFriend();
                initFriend();
                for(int i = 0; i< users.size(); i++){
                    initAcceptFriend(users.get(i).getUid());
                }

            }catch(Exception e){
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
    private void initFriend(){
        firebaseFirestore
                .collection(Constants.KEY_COLLECTION_FRIENDS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_STATUS, 1)
                .addSnapshotListener(eventFriendListener);
    }


    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private final EventListener<QuerySnapshot> eventFriendListener = (value, error) -> {

        if (error != null) {
            return;
        }
        if (value != null) {

            for (DocumentChange documentChange : value.getDocumentChanges()) {

                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    int index = findUser(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    if(index > -1){
                        listFriend.add(users.get(index));
                    }


                } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                    int index = findUser(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    if(index > -1){
                        listFriend.remove(index);
                    }

                }
                if (listFriend.size() > 0) {
                    Users_Adapter users_adapter = new Users_Adapter(listFriend, mainScreenActivity);
                    RecyclerView temp = view.findViewById(R.id.userRecyclerView);
                    temp.setAdapter(users_adapter);
                    temp.setVisibility(View.VISIBLE);
                } else {
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

                } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                    int index = findRequestFriend(documentChange.getDocument().getId());
                    requestFriends.remove(index);
                    RequestAdapter requestAdapter = new RequestAdapter(requestFriends, this);
                    RecyclerView temp = view.findViewById(R.id.requestRecyclerView);
                    temp.setAdapter(requestAdapter);
                }
                if (requestFriends.size() != 0) {
                    view.findViewById(R.id.textRequestFriend).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.textRequestFriend).setVisibility(View.GONE);
                }
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
                }
                else if(documentChange.getType() == DocumentChange.Type.REMOVED){
                    int index = findRequestFriend(documentChange.getDocument().getId());
                    requestFriends.remove(index);
                    RequestAdapter requestAdapter = new RequestAdapter(requestFriends, this);
                    RecyclerView temp = view.findViewById(R.id.requestRecyclerView);
                    temp.setAdapter(requestAdapter);
                    break;
                }
                if (requestFriends.size() != 0) {
                    view.findViewById(R.id.textRequestFriend).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.textRequestFriend).setVisibility(View.GONE);
                }
            }
        }
    };


    private int findUser(String uid) {
        try{
            for (int i = 0; i < users.size(); i++)
                if (users.get(i).getUid().equals(uid))
                    return i;
            return -1;
        }catch (Exception e){
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

        checkForConversionRemotely( receiver, sender);

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
    public void onAcceptFiend(String receiver){
        createAcceptFriend(preferenceManager.getString(Constants.KEY_USER_ID), receiver);
    }

    @Override
    public void onCancelRequestFriend(String uid){
        deleteRequestFriend(uid);
    }

    @Override
    public void onShowUser(String uid) {
        FirebaseFirestore.getInstance()
                .collection(Constants.KEY_COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(v->
                        {
                            User user =new User();
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

}