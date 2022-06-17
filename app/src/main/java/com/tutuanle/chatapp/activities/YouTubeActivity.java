package com.tutuanle.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.tutuanle.chatapp.databinding.ActivityYouTubeBinding;
import com.tutuanle.chatapp.utilities.Constants;

public class YouTubeActivity extends AppCompatActivity {
    private ActivityYouTubeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityYouTubeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getLifecycle().addObserver(binding.youtubePlayerView);



        binding.youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = getIntent().getStringExtra(Constants.KEY_SEND_VIDEO);
                youTubePlayer.loadVideo(videoId, 0);
            }
        });

        binding.imageBack.setOnClickListener(v -> onBackPressed());


    }
}