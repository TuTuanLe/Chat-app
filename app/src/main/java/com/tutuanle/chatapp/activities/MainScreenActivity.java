package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;


import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.databinding.ActivityMainBinding;
import com.tutuanle.chatapp.databinding.ActivityMainScreenBinding;

public class MainScreenActivity extends AppCompatActivity {
    ActivityMainScreenBinding binding;

    private final int ID_HOME = 1;
    private final int ID_MESSAGE = 2;
    private final int ID_NOTIFICATION = 3;
    private final int ID_ACCOUNT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.BottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_baseline_home_24));
        binding.BottomNavigation.add(new MeowBottomNavigation.Model(ID_MESSAGE, R.drawable.ic_baseline_message_24));
        binding.BottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTIFICATION, R.drawable.ic_baseline_notifications_24));
        binding.BottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT, R.drawable.ic_baseline_account_circle_24));

        binding.BottomNavigation.setOnClickMenuListener(item ->
                {}
//                Toast.makeText(getApplicationContext(), "click item :" + item.getId(), Toast.LENGTH_SHORT).show()
        );

        binding.BottomNavigation.setOnShowListener(item -> {
            String name;
            switch (item.getId()) {
                case ID_HOME:
                    name = "Home";
                    break;
                case ID_MESSAGE:
                    name = "Message";
                    break;
                case ID_NOTIFICATION:
                    name = "Notification";
                    break;
                case ID_ACCOUNT:
                    name = "Account";
                    break;
                default:
                    name = "";
            }
            binding.pageSelected.setText(name);

        });
        binding.BottomNavigation.setCount(ID_NOTIFICATION, "4");
        binding.BottomNavigation.show(ID_HOME, true);
    }
}