package com.tutuanle.chatapp.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

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
import com.tutuanle.chatapp.interfaces.ChatListener;
import com.tutuanle.chatapp.models.ChatMessage;
import com.tutuanle.chatapp.models.CustomizeChat;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.network.ApiClient;
import com.tutuanle.chatapp.network.ApiService;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

@RequiresApi(api = Build.VERSION_CODES.N)
public class ChatScreenActivity extends OnChatActivity implements ChatListener {
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
    private CustomizeChat customizeChat;
    private String encodedImage;
    private int TypeMessage = 0;
    private int ScreenResolution = 0;
    private String messageUid;
    private String senderUidMessage;
    int checkRemoveMessage  = 0;
//    ChatListener chatListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadReceiverDetails();
        init();
//
        setListener();
        listenMessages();
        setIconSend();
        customizeYourChat();
        initCustomTheme();
//        updateConversation();
        customChecked();

    }

    private void customChecked() {

        binding.chk1.setOnClickListener(v -> {
            binding.chk1.setChecked(true);
            binding.chk2.setChecked(false);
            binding.chk3.setChecked(false);
            ScreenResolution = 0;
        });
        binding.chk2.setOnClickListener(v -> {
            binding.chk2.setChecked(true);
            binding.chk1.setChecked(false);
            binding.chk3.setChecked(false);
            ScreenResolution = 1;
        });
        binding.chk3.setOnClickListener(v -> {
            binding.chk3.setChecked(true);
            binding.chk1.setChecked(false);
            binding.chk2.setChecked(false);
            ScreenResolution = 2;
        });
    }

    private void loadReceiverDetails() {
        receiverUSer = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUSer.getName());
        binding.imageFriend.setImageBitmap(getBitmapFromEnCodedString(receiverUSer.getProfileImage()));


    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sendMessage());
        binding.iconCloseImage.setOnClickListener(v -> setCloseLayoutChoiseImage());
        binding.imageCall.setOnClickListener(v -> initialAudioMeeting(receiverUSer));
        binding.imageVideoCall.setOnClickListener(v -> initialVideoMeeting(receiverUSer));
        binding.removeMessage.setOnClickListener(v -> openDialogMenu());

    }

    private void customizeYourChat() {
        binding.imageInfo.setOnClickListener(v -> openDialogCenter());
        binding.layoutExtend.setOnClickListener(v -> openDialogBottom());
    }

    private void openDialogBottom() {
        setCloseLayoutChoiseImage();
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_bottom);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        window.setAttributes(windowAttributes);
        dialog.findViewById(R.id.addImageDialog).setOnClickListener(v -> {
            showEmoji();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.addRecordeDialog).setOnClickListener(v ->
                dialog.dismiss()
        );

        dialog.findViewById(R.id.addVideoDialog).setOnClickListener(v ->
                dialog.dismiss()
        );
        dialog.setCancelable(true);
        dialog.show();
    }

    private void openDialogMenu() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_menu);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        window.setAttributes(windowAttributes);

        if (!senderUidMessage.equals(preferenceManager.getString(Constants.KEY_USER_ID))) {
            dialog.findViewById(R.id.layoutUnSend).setVisibility(View.GONE);
            checkRemoveMessage = 0;
        } else {
            dialog.findViewById(R.id.layoutUnSend).setVisibility(View.VISIBLE);
            checkRemoveMessage = 1;
        }

        dialog.findViewById(R.id.UnSend).setOnClickListener(v->{
            FirebaseFirestore.getInstance()
                    .collection(Constants.KEY_COLLECTION_CHAT)
                    .document(messageUid)
                    .update(Constants.KEY_MESSAGE, "You unsent a message" ,
                            Constants.KEY_TYPE_MESSAGE, 3,
                            Constants.KEY_SEND_IMAGE, null,
                            Constants.KEY_FEELING, -1)
                    .addOnSuccessListener(item -> Log.d("UNSEND_MESSAGE", "update successfully"))
                    .addOnFailureListener(item -> Log.d("UNSEND_MESSAGE", "fail "));
            dialog.dismiss();
        });

        dialog.findViewById(R.id.remove).setOnClickListener(v->{
            FirebaseFirestore.getInstance()
                    .collection(Constants.KEY_COLLECTION_CHAT)
                    .document(messageUid)
                    .update( Constants.KEY_TYPE_MESSAGE,  checkRemoveMessage == 1 ?4 : 5)
                    .addOnSuccessListener(item -> Log.d("UNSEND_MESSAGE", "update successfully"))
                    .addOnFailureListener(item -> Log.d("UNSEND_MESSAGE", "fail "));
            dialog.dismiss();
        });

        dialog.setCancelable(true);
        dialog.show();
        binding.cardViewMenu.setVisibility(View.GONE);
    }


    private void openDialogCenter() {
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
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(false);

        dialog.findViewById(R.id.Theme_0).setOnClickListener(v -> {
            ListenerTheme(0);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.Theme_1).setOnClickListener(v -> {
            ListenerTheme(1);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.Theme_2).setOnClickListener(v -> {
            ListenerTheme(2);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.Theme_3).setOnClickListener(v -> {
            ListenerTheme(3);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.Theme_4).setOnClickListener(v -> {
            ListenerTheme(4);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.Theme_5).setOnClickListener(v -> {
            ListenerTheme(5);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.Theme_6).setOnClickListener(v -> {
            ListenerTheme(6);
            dialog.dismiss();
        });
        dialog.findViewById(R.id.icon_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        customizeChat = new CustomizeChat();

        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEnCodedString(receiverUSer.getProfileImage()),
                preferenceManager.getString(Constants.KEY_USER_ID),
                this);

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

    private void ListenerTheme(int theme) {
        FirebaseFirestore.getInstance()
                .collection(Constants.KEY_COLLECTION_CUSTOM_CHAT)
                .document(customizeChat.getCustomizeUid())
                .update(Constants.KEY_THEME, theme);
        binding.setImageScreen.setBackgroundResource(0);
        database.collection(Constants.KEY_COLLECTION_CUSTOM_CHAT)
                .whereEqualTo(Constants.KEY_USER_UID_1, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_USER_UID_2, receiverUSer.getUid())
                .addSnapshotListener(eventListenerTheme);

        database.collection(Constants.KEY_COLLECTION_CUSTOM_CHAT)
                .whereEqualTo(Constants.KEY_USER_UID_1, receiverUSer.getUid())
                .whereEqualTo(Constants.KEY_USER_UID_2, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListenerTheme);
    }


    private void initCustomTheme() {
        database.collection(Constants.KEY_COLLECTION_CUSTOM_CHAT)
                .whereEqualTo(Constants.KEY_USER_UID_1, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_USER_UID_2, receiverUSer.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                        int KeyTheme = Integer.parseInt(Objects.requireNonNull(snapshot.getLong(Constants.KEY_THEME)).toString());
                        customizeChat.setCustomizeUid(snapshot.getId());
                        customizeChat.setTheme(KeyTheme);
                        customizeChat.setGradient(snapshot.getString(Constants.KEY_GRADIENT));
                        binding.setImageScreen.setBackgroundResource(Constants.THEMES[KeyTheme]);
                        ListenerTheme(KeyTheme);
                    } else {
                        database.collection(Constants.KEY_COLLECTION_CUSTOM_CHAT)
                                .whereEqualTo(Constants.KEY_USER_UID_1, receiverUSer.getUid())
                                .whereEqualTo(Constants.KEY_USER_UID_2, preferenceManager.getString(Constants.KEY_USER_ID))
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful() && task1.getResult() != null && task1.getResult().getDocuments().size() > 0) {
                                        DocumentSnapshot snapshot = task1.getResult().getDocuments().get(0);
                                        int KeyTheme = Integer.parseInt(Objects.requireNonNull(snapshot.getLong(Constants.KEY_THEME)).toString());
                                        customizeChat.setCustomizeUid(snapshot.getId());
                                        customizeChat.setTheme(KeyTheme);
                                        customizeChat.setGradient(snapshot.getString(Constants.KEY_GRADIENT));
                                        binding.setImageScreen.setBackgroundResource(Constants.THEMES[KeyTheme]);
                                        ListenerTheme(KeyTheme);
                                    } else {
                                        HashMap<String, Object> cusChat = new HashMap<>();
                                        cusChat.put(Constants.KEY_USER_UID_1, preferenceManager.getString(Constants.KEY_USER_ID));
                                        cusChat.put(Constants.KEY_USER_UID_2, receiverUSer.getUid());
                                        cusChat.put(Constants.KEY_THEME, 0);
                                        cusChat.put(Constants.KEY_GRADIENT, "#fff");

                                        database.collection(Constants.KEY_COLLECTION_CUSTOM_CHAT)
                                                .add(cusChat)
                                                .addOnSuccessListener(v -> {
                                                    customizeChat.setCustomizeUid(v.getId());
                                                    customizeChat.setTheme(0);
                                                    customizeChat.setGradient("#fff");
                                                    ListenerTheme(0);
                                                })
                                        ;

                                    }
                                });
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
                    try {
                        chatMessage.setIsSeen(Integer.parseInt(Objects.requireNonNull(documentChange.getDocument().getLong(Constants.KEY_IS_SEEN)).toString()));
                    } catch (Exception e) {
                        chatMessage.setIsSeen(0);
                    }
                    try {
                        chatMessage.setTypeMessage(Integer.parseInt(Objects.requireNonNull(documentChange.getDocument().getLong(Constants.KEY_TYPE_MESSAGE)).toString()));
                    } catch (Exception e) {
                        chatMessage.setTypeMessage(0);
                    }
                    try {
                        if (chatMessage.getTypeMessage() == 1) {
                            chatMessage.setImageBitmap(documentChange.getDocument().getString(Constants.KEY_SEND_IMAGE));
                        } else if (chatMessage.getTypeMessage() == 2) {
                            chatMessage.setUrlVideo(documentChange.getDocument().getString(Constants.KEY_SEND_VIDEO));
                        } else if (chatMessage.getTypeMessage() == 3) {
                            chatMessage.setUrlRecord(documentChange.getDocument().getString(Constants.KEY_SEND_RECORD));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    chatMessages.add(chatMessage);

                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    String docID = documentChange.getDocument().getId();
                    int index = findMessage(docID);
                    chatMessages.get(index).setFeeling(
                            Integer.parseInt(Objects.requireNonNull(documentChange.getDocument().getLong(Constants.KEY_FEELING)).toString()));
                    chatMessages.get(index).setMessage(documentChange.getDocument().getString(Constants.KEY_MESSAGE));
                    chatMessages.get(index).setTypeMessage(Integer.parseInt(Objects.requireNonNull(documentChange.getDocument().getLong(Constants.KEY_TYPE_MESSAGE)).toString()));
                    if (chatMessages.get(index).getTypeMessage() == 1) {
                        chatMessages.get(index).setImageBitmap(documentChange.getDocument().getString(Constants.KEY_SEND_IMAGE));
                    } else if (chatMessages.get(index).getTypeMessage() == 2) {
                        chatMessages.get(index).setImageBitmap(documentChange.getDocument().getString(Constants.KEY_SEND_VIDEO));
                    }



                    chatAdapter.notifyDataSetChanged();
                    binding.cardViewMenu.setVisibility(View.GONE);
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


    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListenerTheme = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                int KeyTheme = Integer.parseInt(String.valueOf(documentChange.getDocument().getLong(Constants.KEY_THEME)));
                binding.setImageScreen.setBackgroundResource(Constants.THEMES[KeyTheme]);
            }
        }
    };


    private void setCloseLayoutChoiseImage() {
        binding.ImageChoice.setBackgroundResource(0);
        binding.layoutChoiceImage.setVisibility(View.GONE);
        binding.inputMessage.setText(null);
        TypeMessage = 0;
    }

    private void showEmoji() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);

    }

    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    assert result.getData() != null;
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        encodedImage = encodeImage(bitmap);
                        binding.layoutChoiceImage.setVisibility(View.VISIBLE);
                        binding.ImageChoice.setImageBitmap(bitmap);
                        TypeMessage = 1;
                        binding.inputMessage.setText("\uD83D\uDD16" + " " + preferenceManager.getString(Constants.KEY_NAME) + "  sent a photo .");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        TypeMessage = 0;
                    }
                }
            }
    );

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 300;
        if (ScreenResolution == 1) {
            previewWidth = 500;
        } else if (ScreenResolution == 2) {
            previewWidth = 700;
        }
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    private int findMessage(String uid) {
        for (int i = 0; i < chatMessages.size(); i++)
            if (chatMessages.get(i).getMessageId().equals(uid))
                return i;
        return -1;
    }


//    private void updateConversation() {
//        if (isOnChat && chatMessages.size() != 0) {
//            FirebaseFirestore.getInstance()
//                    .collection(Constants.KEY_COLLECTION_CHAT)
//                    .document(chatMessages.get(chatMessages.size() - 1).getMessageId())
//                    .update(Constants.KEY_IS_SEEN, 0)
//                    .addOnSuccessListener(item -> Log.d("IS_SEEN", "update successfully"))
//                    .addOnFailureListener(item -> Log.d("IS_SEEN", "fail "));
//        }
//    }


    private void sendMessage() {

        String ms = binding.inputMessage.getText().length() == 0 ? "\uD83D\uDC4D" : binding.inputMessage.getText().toString();
        HashMap<String, Object> message = new HashMap<>();
        // get senderId from Preference manage on save a local app
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUSer.getUid());
        message.put(Constants.KEY_MESSAGE, ms);
        message.put(Constants.KEY_TIMESTAMP, new Date());
        message.put(Constants.KEY_FEELING, -1);
        message.put(Constants.KEY_IS_SEEN, isOnChat ? 0 : 1);
        message.put(Constants.KEY_TYPE_MESSAGE, TypeMessage);
        //send image
        if (TypeMessage == 1) {
            message.put(Constants.KEY_SEND_IMAGE, encodedImage);
            setCloseLayoutChoiseImage();
        } else {
            message.put(Constants.KEY_SEND_IMAGE, null);
        }
//        send video
        if (TypeMessage == 2) {
            message.put(Constants.KEY_SEND_VIDEO, "send video");
        } else {
            message.put(Constants.KEY_SEND_VIDEO, null);
        }
        // send record
        if (TypeMessage == 3) {
            message.put(Constants.KEY_SEND_RECORD, "send record");
        } else {
            message.put(Constants.KEY_SEND_RECORD, null);
        }

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
                showToast("Duoi phuong chua hoat dong" + e.getMessage());
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
                            int onChat = Objects.requireNonNull(value.getLong(Constants.KEY_ON_CHAT)).intValue();
                            isOnChat = onChat == 1;
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
                showToast("hello" + t.getMessage());
            }
        });
    }

    @Override
    public void initialVideoMeeting(User user) {
        if (user.getToken() == null || user.getToken().trim().isEmpty()) {
            showToast(user.getName() + " is not available for meeting ...");

        } else {
            showToast(user.getName() + " video call ...");
            Intent intent = new Intent(getApplicationContext(), OutgoingActivity.class);
            intent.putExtra(Constants.KEY_USER, user);
            intent.putExtra("type_call", "video");
            startActivity(intent);
        }
    }

    @Override
    public void initialAudioMeeting(User user) {
        if (user.getToken() == null || user.getToken().trim().isEmpty()) {
            showToast(user.getName() + " is not available for calling ...");
        } else {
            showToast(user.getName() + " call ...");
            Intent intent = new Intent(getApplicationContext(), OutgoingActivity.class);
            intent.putExtra(Constants.KEY_USER, user);
            intent.putExtra("type_call","audio");
            startActivity(intent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }

    @Override
    public void onClickShowImage(String url) {
        Intent intent = new Intent(getApplicationContext(), ShowImageActivity.class);
        intent.putExtra(Constants.KEY_IMAGE, url);
        startActivity(intent);
    }

    @Override
    public void onLongClickRemoveMessage(String uidMessage, String senderId) {
        binding.cardViewMenu.setVisibility(View.VISIBLE);
        messageUid = uidMessage;
        senderUidMessage = senderId;
    }

    @Override
    public void showAnimationReaction(int index) {
        binding.animationView.setVisibility(View.VISIBLE);
        binding.animationView.setAnimation(Constants.REACTIONS_ANIMATION[index]);
        binding.animationView.playAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.animationView.pauseAnimation();
                binding.animationView.setVisibility(View.GONE);
            }
        }, 2000);

    }


}