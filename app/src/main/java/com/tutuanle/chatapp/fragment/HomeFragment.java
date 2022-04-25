package com.tutuanle.chatapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.errorprone.annotations.Var;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.activities.MainScreenActivity;
import com.tutuanle.chatapp.adapters.HomeFriendAdapter;
import com.tutuanle.chatapp.adapters.TopStatusAdapter;
import com.tutuanle.chatapp.adapters.Users_Adapter;
import com.tutuanle.chatapp.models.ChatMessage;
import com.tutuanle.chatapp.models.Status;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.models.UserStatus;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View view;

    private MainScreenActivity mainScreenActivity;
    private PreferenceManager preferenceManager;

    private ArrayList<UserStatus> userStatuses;
    private TopStatusAdapter statusAdapter;
    private HomeFriendAdapter homeFriendAdapter;
    private FirebaseFirestore database;
    private List<ChatMessage> listFriends;
    private String conversionId = null;
    private User receiverUser;


    public HomeFragment() {

    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        mainScreenActivity = (MainScreenActivity) getActivity();
        assert mainScreenActivity != null;
        preferenceManager = mainScreenActivity.preferenceManager;

        initialData();
        getUSer();
        getUserStatus();
        return view;
    }

    private void initialData() {

        TextView temp = view.findViewById(R.id.nameTextView);
        temp.setText(mainScreenActivity.getTextName());
        RoundedImageView image = view.findViewById(R.id.imageProfile);
        image.setImageBitmap(mainScreenActivity.getBitmap());
    }

    private void initFriend() {
        listFriends = new ArrayList<>();
        homeFriendAdapter = new HomeFriendAdapter(listFriends);
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


    private void getUSer() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
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
                            users.add(user);

                        }
                        if (users.size() > 0) {
                            Users_Adapter users_adapter = new Users_Adapter(users, mainScreenActivity);

                            RecyclerView temp = view.findViewById(R.id.userRecyclerView);
                            temp.setAdapter(users_adapter);
                            temp.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    }
                });
    }

    private void loading(Boolean isLoading) {
        ProgressBar temp = view.findViewById(R.id.progressBar);
        if (isLoading) {
            temp.setVisibility(View.VISIBLE);
        } else {
            temp.setVisibility(View.INVISIBLE);
        }
    }

    private void showErrorMessage() {
        TextView temp = view.findViewById(R.id.textErrorMessage);
        temp.setText("Not exist");
        temp.setVisibility(View.VISIBLE);
    }

    private void checkForConversion(){
        if(listFriends.size() != 0  ){
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    receiverUser.getUid()
            );
        }else{
            checkForConversionRemotely(
                    receiverUser.getUid(),
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }


    private void checkForConversionRemotely(String senderId, String receiverId){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }


    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener  = task -> {
        if(task.isSuccessful() && task.getResult() != null  && task.getResult().getDocuments().size() > 0  ){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }
    };
}