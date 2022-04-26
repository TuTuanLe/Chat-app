package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tutuanle.chatapp.adapters.ChatAdapter;
import com.tutuanle.chatapp.databinding.ActivityChatScreenBinding;
import com.tutuanle.chatapp.models.ChatMessage;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatScreenActivity extends AppCompatActivity {
    private ActivityChatScreenBinding binding;
    private User receiverUSer;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private List<ChatMessage> chatMessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadReceiverDetails();
        setListener();
        init();
        listenMessages();
    }

    private void loadReceiverDetails() {
        receiverUSer = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUSer.getName());
        binding.imageFriend.setImageBitmap(getBitmapFromEnCodedString(receiverUSer.getProfileImage()));
    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEnCodedString(receiverUSer.getProfileImage()),
                preferenceManager.getString(Constants.KEY_USER_ID)
        );

        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private Bitmap getBitmapFromEnCodedString(String enCodedImage) {
        byte[] bytes = Base64.decode(enCodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    private void listenMessages() {

        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUSer.getUid())
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUSer.getUid())
                .addSnapshotListener(eventListener);

    }


    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = chatMessages.size();

            for (DocumentChange documentChange : value.getDocumentChanges()) {

                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMessageId(documentChange.getDocument().getId());
                    chatMessage.setSenderId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                    chatMessage.setReceiverId(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    chatMessage.setMessage(documentChange.getDocument().getString(Constants.KEY_MESSAGE));
                    chatMessage.setFeeling(Integer.parseInt(Objects.requireNonNull(documentChange.getDocument().getLong(Constants.KEY_FEELING)).toString()));
                    chatMessage.setDateTime(getReadableDatetime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP)));
                    chatMessage.dataObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
                 else if(documentChange.getType() == DocumentChange.Type.MODIFIED){
                    String docID = documentChange.getDocument().getId();

                    chatMessages.get(findMessage(docID)).setFeeling(
                            Integer.parseInt(  Objects.requireNonNull(documentChange.getDocument().getLong(Constants.KEY_FEELING)).toString()) );

                    Log.d("FEELING_TEST", docID  + "  -  " + findMessage(docID) );
                    chatAdapter.notifyDataSetChanged();
                } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
//
                    // remove
//                    String docID = documentChange.getDocument().getId();
                    chatMessages.remove(documentChange.getOldIndex());
                    chatAdapter.notifyItemRemoved(documentChange.getOldIndex());
                }

            }
            Collections.sort(chatMessages, (x, y) -> x.dataObject.compareTo(y.dataObject));

            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
    };

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sendMessage());
    }

    private  int findMessage(String uid) {
        for(int i=0; i<chatMessages.size(); i++)
            if(chatMessages.get(i).getMessageId().equals(uid))
                return i;
        return -1;
    }

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        // get senderId from Preference manage on save a local app
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        // get intent when on click user
        message.put(Constants.KEY_RECEIVER_ID, receiverUSer.getUid());
        // input text get line from keyboard
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        // create 1 timestamp
        message.put(Constants.KEY_TIMESTAMP, new Date());
        // default not exist feeling = -1
        message.put(Constants.KEY_FEELING, -1);

        database.collection(Constants.KEY_COLLECTION_CHAT)
                .add(message);
        binding.inputMessage.setText(null);
        binding.inputMessage.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    private String getReadableDatetime(Date date) {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);
    }
}