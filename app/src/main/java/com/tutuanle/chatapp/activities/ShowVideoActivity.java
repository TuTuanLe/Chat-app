package com.tutuanle.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;

import com.tutuanle.chatapp.databinding.ActivityShowVideoBinding;
import com.tutuanle.chatapp.utilities.Constants;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ShowVideoActivity extends AppCompatActivity {
    private ActivityShowVideoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListener();
    }

    private  void init(){
        String url = getIntent().getStringExtra(Constants.KEY_SEND_VIDEO);

        binding.showVideo.setVideoURI(Uri.parse(url));
        binding.showVideo.requestFocus();
        binding.showVideo.start();
        binding.showVideo.setOnPreparedListener(mediaPlayer -> {
            try {
                binding.progressBar.setVisibility(View.GONE);
                binding.showVideo.start();

            } catch (Exception e) {
                // TODO: handle exception

            }
        });
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(binding.showVideo);
        binding.showVideo.setMediaController(mediaController);


    }


    public void setListener(){

        binding.showVideo.setOnClickListener(v -> {
            binding.showVideo.start();
        });
    }


}