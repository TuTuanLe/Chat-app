package com.tutuanle.chatapp.activities;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.Toast;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.databinding.ActivityMainScreenBinding;
import com.tutuanle.chatapp.fragment.ChatFragment;
import com.tutuanle.chatapp.fragment.HomeFragment;
import com.tutuanle.chatapp.fragment.SettingFragment;
import com.tutuanle.chatapp.fragment.StoryFragment;
import com.tutuanle.chatapp.interfaces.FriendListener;
import com.tutuanle.chatapp.interfaces.UserListener;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;
import java.util.HashMap;


public class MainScreenActivity extends BaseActivity  implements UserListener , FriendListener {
    private ActivityMainScreenBinding binding;
    public PreferenceManager preferenceManager;

    private final int ID_HOME = 1;
    private final int ID_MESSAGE = 2;
    private final int ID_NOTIFICATION = 3;
    private final int ID_ACCOUNT = 4;
    private String textName ;
    private Bitmap bitmap;



    public String getTextName() {
        return textName;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setBottomNavigation();
        loadUserDetail();
        getToken();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    private void loadUserDetail(){
        textName = preferenceManager.getString(Constants.KEY_NAME);
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(bytes, 0,bytes.length);
    }




    private void setBottomNavigation (){
        binding.BottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_baseline_home_24));
        binding.BottomNavigation.add(new MeowBottomNavigation.Model(ID_MESSAGE, R.drawable.ic_baseline_message_24));
        binding.BottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTIFICATION, R.drawable.ic_baseline_notifications_24));
        binding.BottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT, R.drawable.ic_baseline_account_circle_24));

        binding.BottomNavigation.setOnClickMenuListener(item -> {});
        binding.BottomNavigation.setOnReselectListener(item -> {});

        binding.BottomNavigation.setOnShowListener(item -> {
            Fragment fragment = null;
            switch (item.getId()) {
                case ID_HOME:
                    fragment = new HomeFragment();
                    break;
                case ID_MESSAGE:
                    fragment = new ChatFragment();
                    break;
                case ID_NOTIFICATION:
                    fragment = new StoryFragment();
                    break;
                case ID_ACCOUNT:
                    fragment = new SettingFragment();
                    break;
            }
            loadFragment(fragment);

        });
        binding.BottomNavigation.setCount(ID_HOME, "4");
        binding.BottomNavigation.show(ID_HOME, true);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    private void showToast(String Message){
        Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_SHORT).show();
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private  void updateToken(String token)
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener( item-> {})
                .addOnFailureListener(item->showToast("Unable to update token"));
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
    }

    public void  signOut(){
        showToast("Sign out ");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(item->{
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                }
                )
                .addOnFailureListener(e ->showToast("Unable to sign out"));

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatScreenActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
//        finish();
    }

    @Override
    public void initialVideoMeeting(User user) {
        if(user.getToken() == null || user.getToken().trim().isEmpty()){
            showToast(user.getName() + " is not available for meeting ...");

        }
        else{
            showToast(user.getName() + " video call ...");
            Intent intent = new Intent(getApplicationContext(), OutgoingActivity.class);
            intent.putExtra(Constants.KEY_USER, user);
            intent.putExtra("type_call", "video");
            startActivity(intent);
        }

    }

    @Override
    public void initialAudioMeeting(User user) {
        if(user.getToken() == null || user.getToken().trim().isEmpty()){
            showToast(user.getName() + " is not available for calling ...");
        }
        else{
            showToast(user.getName() + " call ...");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onFriendClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatScreenActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}