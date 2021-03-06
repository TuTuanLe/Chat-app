package com.tutuanle.chatapp.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

public abstract class OnChatActivity  extends AppCompatActivity {


    public  OnChatActivity(){

    }

    private DocumentReference documentReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
    }

    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update(Constants.KEY_ON_CHAT, 0);
        documentReference.update(Constants.KEY_AVAILABILITY, 0);
    }

    public abstract void initialVideoMeeting(User user);

    public abstract void initialAudioMeeting(User user);

    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update(Constants.KEY_ON_CHAT,1);
        documentReference.update(Constants.KEY_AVAILABILITY, 1);
    }
}
