package com.tutuanle.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.tutuanle.chatapp.databinding.ActivityOutgoingBinding;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.network.ApiClient;
import com.tutuanle.chatapp.network.ApiService;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingActivity extends AppCompatActivity {
    ActivityOutgoingBinding binding;
    private User receiverUSer;
    private PreferenceManager preferenceManager;
    private String inviterToken = null;
    private String meetingType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOutgoingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialData();
        setupCall();
        setOnclickListener();
    }

    private void setOnclickListener() {
        binding.imageStop.setOnClickListener(v -> onBackPressed());
        binding.info.setText(receiverUSer.getName() + "call video");
    }

    private void initialData() {
        receiverUSer = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        meetingType = getIntent().getStringExtra("type_call");
        preferenceManager = new PreferenceManager(getApplicationContext());

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                inviterToken = task.getResult().getToken();
            }
        });
    }


    private void setupCall() {

        if (meetingType != null && receiverUSer != null) {
            initialMeeting(meetingType, receiverUSer.getToken());
        }
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
            data.put(Constants.KEY_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);
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

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}