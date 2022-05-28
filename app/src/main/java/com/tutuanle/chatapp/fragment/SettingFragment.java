package com.tutuanle.chatapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.activities.MainScreenActivity;
import com.tutuanle.chatapp.activities.ProfileActivity;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;


public class SettingFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View view;
    private User user;

    private MainScreenActivity mainScreenActivity;
    private GoogleSignInClient mGoogleSignInClient;

    public SettingFragment() {

    }


    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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

        view=  inflater.inflate(R.layout.fragment_setting, container, false);
        mainScreenActivity =(MainScreenActivity) getActivity();
        initialData();
        setListeners();
        openProfile();
        return view;
    }

    private void openProfile() {

        FrameLayout frameAccount = view.findViewById(R.id.frameAccount);

        frameAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenProfile = new Intent(getContext(), ProfileActivity.class);
                user = new User();
                user.setName(mainScreenActivity.preferenceManager.getString(Constants.KEY_NAME));
                user.setUid(mainScreenActivity.preferenceManager.getString(Constants.KEY_USER_ID));
                user.setProfileImage(mainScreenActivity.preferenceManager.getString(Constants.KEY_IMAGE));
                user.setPhoneNumber(mainScreenActivity.preferenceManager.getString(Constants.KEY_NUMBER_PHONE));
                user.setPassword(mainScreenActivity.preferenceManager.getString(Constants.KEY_PASSWORD));
                user.setEmail(mainScreenActivity.preferenceManager.getString(Constants.KEY_EMAIL));
                intenProfile.putExtra(Constants.KEY_USER,user);
                startActivity(intenProfile);
            }
        });

    }


    private void initialData() {

        TextView temp= view.findViewById(R.id.nameProfile);
        temp.setText(mainScreenActivity.getTextName());
        RoundedImageView image = view.findViewById(R.id.imageProfile);
        image.setImageBitmap(mainScreenActivity.getBitmap());


    }

    private void setListeners(){
        FrameLayout frameLayout =  view.findViewById(R.id.logout);
        frameLayout.setOnClickListener( v-> {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            mGoogleSignInClient = GoogleSignIn.getClient(mainScreenActivity, gso);
            mainScreenActivity.signOut();
            mGoogleSignInClient.signOut();
        });
    }
}