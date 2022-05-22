package com.tutuanle.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.tutuanle.chatapp.databinding.ActivityIncomingBinding;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.network.ApiClient;
import com.tutuanle.chatapp.network.ApiService;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingActivity extends AppCompatActivity {

    ActivityIncomingBinding binding;
    private User receiverUSer;
    private String meetingType;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncomingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupCall();
        setBindingListener();
    }

    private void setupCall() {
        receiverUSer = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        meetingType = getIntent().getStringExtra("type_call");

        preferenceManager = new PreferenceManager(getApplicationContext());

        String meetingType = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);

        binding.info.setText(getIntent().getStringExtra(Constants.KEY_NAME));
        binding.imgAvatar.setImageBitmap(getConversionImage(getIntent().getStringExtra(Constants.KEY_IMAGE)));

        if(meetingType != null){
            if(meetingType.equals("video")){
                // set up sent info
            }
        }
    }


    private Bitmap getConversionImage(String encodedImage)
    {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
    private void setBindingListener(){
        binding.imageAccept.setOnClickListener(v ->AcceptVideoCall());
        binding.imageStop.setOnClickListener(v->StopVideoCall());
    }

    private void AcceptVideoCall(){
        sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_ACCEPTED,getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN) );
    }

    private  void StopVideoCall(){
        sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_REJECTED,getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN) );
    }

    private void sendInvitationResponse(String type, String receiverToken){
        try{
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type);
            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), type);
        }catch(Exception e){
            showToast(e.getMessage());
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
                    if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {
                        // invitation accepted
                        try{
                            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                                    .setServerURL(new URL("https://meet.jit.si"))
                                    .setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROM))
                                    .setWelcomePageEnabled(false)
                                    .build();
                            JitsiMeetActivity.launch(IncomingActivity.this, options);
                            finish();
                        }catch(Exception e){
                            showToast(e.getMessage());
                            finish();
                        }

                    }else if(type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)){
                        // invitation rejected
                        finish();
                    }
                } else {
                    showToast("No internet");
                    finish();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showToast("hello" + t.getMessage());
                finish();
            }
        });
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private final BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)) {
                    showToast("Invitation Cancelled");
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