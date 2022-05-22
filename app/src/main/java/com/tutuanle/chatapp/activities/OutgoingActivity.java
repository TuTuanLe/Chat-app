package com.tutuanle.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.tutuanle.chatapp.databinding.ActivityOutgoingBinding;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.network.ApiClient;
import com.tutuanle.chatapp.network.ApiService;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingActivity extends AppCompatActivity {
    ActivityOutgoingBinding binding;
    private User receiverUSer;
    private PreferenceManager preferenceManager;
    private String inviterToken = null;
    private String meetingType = null;
    private String meetingRoom  = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOutgoingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialData();
        setupCall();
        setOnclickListener();
    }

    @SuppressLint("SetTextI18n")
    private void setOnclickListener() {
        binding.imageStop.setOnClickListener(v -> {
            if (receiverUSer != null) {
                cancelInvitation(receiverUSer.getToken());
            }
        });
        binding.info.setText(receiverUSer.getName());
        binding.imgAvatar.setImageBitmap(getConversionImage(receiverUSer.getProfileImage()));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private Bitmap getConversionImage(String encodedImage)
    {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void initialData() {
        receiverUSer = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        meetingType = getIntent().getStringExtra("type_call");


        preferenceManager = new PreferenceManager(getApplicationContext());
    }


    private void setupCall() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                inviterToken = task.getResult().getToken();
                if (meetingType != null && receiverUSer != null) {
                    initialMeeting(meetingType, receiverUSer.getToken());
                }
            }
        });
       
    }

    private void initialMeeting(String meetingType, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(Constants.KEY_NAME, preferenceManager.getString(Constants.KEY_NAME));
            data.put(Constants.KEY_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);

            meetingRoom = preferenceManager.getString(Constants.KEY_USER_ID)+ "_"+
                    UUID.randomUUID().toString().substring(0,5);
            data.put(Constants.REMOTE_MSG_MEETING_ROM, meetingRoom);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION);
        } catch (Exception e) {
            showToast(e.getMessage());
            finish();
        }

    }


    private void sendRemoteMessage(String messageBody, String type) {
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
                        showToast("Invitation sent successfully");
                    } else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)) {
                        showToast("Invitation cancelled");
                        finish();
                    }
                } else {
                    showToast("Error" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showToast("hello" + t.getMessage());
                finish();
            }
        });
    }

    private void cancelInvitation(String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, Constants.REMOTE_MSG_INVITATION_CANCELLED);
            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION_RESPONSE);
        } catch (Exception e) {
            showToast(e.getMessage());
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private final BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {
                    //                    invitation accepted

                    try {
                        JitsiMeetConferenceOptions  options = new JitsiMeetConferenceOptions.Builder()
                                .setServerURL(new URL("https://meet.jit.si"))
                                .setRoom(meetingRoom)
                                .setWelcomePageEnabled(false)
                                .build();
                        JitsiMeetActivity.launch(context, options);
                        finish();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                } else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)) {
                    showToast("Invitation Reject");
                    finish();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }
}
