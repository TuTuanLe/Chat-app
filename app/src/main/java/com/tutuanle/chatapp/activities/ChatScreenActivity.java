package com.tutuanle.chatapp.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.adapters.ChatAdapter;
import com.tutuanle.chatapp.databinding.ActivityChatScreenBinding;
import com.tutuanle.chatapp.models.ChatMessage;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.network.ApiClient;
import com.tutuanle.chatapp.network.ApiService;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatScreenActivity extends OnChatActivity {
    private ActivityChatScreenBinding binding;
    private User receiverUSer;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private List<ChatMessage> chatMessages;
    private String conversionId = null;
    private Boolean isReceiverAvailable = false;
    private Boolean isOnChat = false;
    private Integer countMessage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadReceiverDetails();
        setListener();
        init();
        listenMessages();
        setIconSend();
        customizeYourChat();
    }

    private void loadReceiverDetails() {
        receiverUSer = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUSer.getName());
        binding.imageFriend.setImageBitmap(getBitmapFromEnCodedString(receiverUSer.getProfileImage()));
    }

    private void customizeYourChat() {
        binding.imageInfo.setOnClickListener(v -> {
            openDialog(Gravity.CENTER);
        });
    }

    private void openDialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_theme);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(Gravity.BOTTOM == gravity);

        dialog.findViewById(R.id.Theme_default).setOnClickListener(v -> {
            binding.setImageScreen.setBackgroundResource(0);
            dialog.dismiss();
        });

        dialog.findViewById(R.id.Theme_chill).setOnClickListener(v -> {
            binding.setImageScreen.setBackgroundResource(R.drawable.bg_3);
            dialog.dismiss();
        });

        dialog.findViewById(R.id.Theme_love).setOnClickListener(v -> {
            binding.setImageScreen.setBackgroundResource(R.drawable.bg_4);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.Theme_sky).setOnClickListener(v -> {
            binding.setImageScreen.setBackgroundResource(R.drawable.bg_sky);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.icon_close).setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
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
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUSer.getUid())
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUSer.getUid())
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

    private void setIconSend() {
        binding.inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    binding.iconSend.setColorFilter(ContextCompat.getColor(ChatScreenActivity.this, R.color.blue), android.graphics.PorterDuff.Mode.MULTIPLY);

                } else {
                    binding.iconSend.setColorFilter(ContextCompat.getColor(ChatScreenActivity.this, R.color.colorGray), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    String docID = documentChange.getDocument().getId();

                    chatMessages.get(findMessage(docID)).setFeeling(
                            Integer.parseInt(Objects.requireNonNull(documentChange.getDocument().getLong(Constants.KEY_FEELING)).toString()));

                    chatAdapter.notifyDataSetChanged();
                } else if (documentChange.getType() == DocumentChange.Type.REMOVED) {
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
        if (conversionId == null) {
            checkForConversion();
        }
    };

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sendMessage());
        binding.btnEmoji.setOnClickListener(v -> showEmoji());
    }

    private void showEmoji() {
        binding.inputMessage.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_PICTSYMBOLS));
        showToast("hello");
    }

    private int findMessage(String uid) {
        for (int i = 0; i < chatMessages.size(); i++)
            if (chatMessages.get(i).getMessageId().equals(uid))
                return i;
        return -1;
    }

    private void sendMessage() {

        String ms = binding.inputMessage.getText().length() == 0 ? "\uD83D\uDC4D" : binding.inputMessage.getText().toString();
        HashMap<String, Object> message = new HashMap<>();
        // get senderId from Preference manage on save a local app
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUSer.getUid());
        message.put(Constants.KEY_MESSAGE, ms);
        message.put(Constants.KEY_TIMESTAMP, new Date());
        message.put(Constants.KEY_FEELING, -1);

        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);

        // set recent chat
        if (conversionId != null) {
            updateConversion(ms);
        } else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID, receiverUSer.getUid());
            conversion.put(Constants.KEY_RECEIVER_NAME, receiverUSer.getName());
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receiverUSer.getProfileImage());
            conversion.put(Constants.KEY_LAST_MESSAGE, ms);
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            conversion.put(Constants.KEY_COUNT_NUMBER_OF_MESSAGE_SEEN, "0");
            conversion.put(Constants.KEY_IS_ACTIVE, preferenceManager.getString(Constants.KEY_USER_ID));
            addConversion(conversion);
        }

        if (!isReceiverAvailable) {
            try {
                JSONArray tokens = new JSONArray();
                tokens.put(receiverUSer.getToken());
                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA, data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);
                sendNotification(body.toString());

            } catch (Exception e) {
                showToast(e.getMessage());
            }
        }
        binding.inputMessage.setText(null);
        binding.inputMessage.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    private String getReadableDatetime(Date date) {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversion(HashMap<String, Object> conversion) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(value -> conversionId = value.getId());
    }

    private void updateConversion(String message) {
        checkForConversion();
        countMessage = (isOnChat ? 0 : countMessage + 1);


        Log.d("TAG_CHAT_countMessage", countMessage.toString());
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
        documentReference.
                update(
                        Constants.KEY_IS_ACTIVE, preferenceManager.getString(Constants.KEY_USER_ID),
                        Constants.KEY_LAST_MESSAGE, message,
                        Constants.KEY_TIMESTAMP, new Date(),
                        Constants.KEY_COUNT_NUMBER_OF_MESSAGE_SEEN, countMessage.toString()
                );
    }


    private void checkForConversion() {
        checkForConversionRemotely(
                preferenceManager.getString(Constants.KEY_USER_ID),
                receiverUSer.getUid()
        );
        if (conversionId == null) {
            checkForConversionRemotely(
                    receiverUSer.getUid(),
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    private void checkForConversionRemotely(String senderId, String receiverId) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
            countMessage = Integer.valueOf(Objects.requireNonNull(documentSnapshot.getString(Constants.KEY_COUNT_NUMBER_OF_MESSAGE_SEEN)));
        }
    };

    private void listenAvailabilityOfReceiver() {
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(receiverUSer.getUid())
                .addSnapshotListener(ChatScreenActivity.this, (value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        if (value.getLong(Constants.KEY_AVAILABILITY) != null) {
                            int availability = Objects.requireNonNull(value.getLong(Constants.KEY_AVAILABILITY)).intValue();
                            isReceiverAvailable = availability == 1;

                        }

                        if (value.getLong(Constants.KEY_ON_CHAT) != null) {
                            int onchat = Objects.requireNonNull(value.getLong(Constants.KEY_ON_CHAT)).intValue();
                            isOnChat = onchat == 1;
                            Log.d("TAG_CHAT", "listenAvailabilityOfReceiver: " + isOnChat);
                        }

                        receiverUSer.setToken(value.getString(Constants.KEY_FCM_TOKEN));
                    }
                    if (isReceiverAvailable) {
                        binding.statusAvailability.setVisibility(View.VISIBLE);
                    } else {
                        binding.statusAvailability.setVisibility(View.GONE);
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String messageBody) {
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body() != null) {
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if (response.body() != null) {
                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    showToast("Error" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showToast(t.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }
}