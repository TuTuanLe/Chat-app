package com.tutuanle.chatapp.fragment;

import static org.webrtc.ContextUtils.getApplicationContext;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.activities.MainScreenActivity;
import com.tutuanle.chatapp.activities.ShowImgStory;
import com.tutuanle.chatapp.adapters.StoryAdapter;
import com.tutuanle.chatapp.models.Status;
import com.tutuanle.chatapp.models.UserStatus;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.GridSpacingItemDecoration;
import com.tutuanle.chatapp.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;


public class StoryFragment extends Fragment {

    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_FIRST_USER = 1;
    public static final int RESULT_OK = -1;
    private String encodedImage ="";

    private FrameLayout layoutChoiceImageStory;
    private RoundedImageView ImageStoryChoice;
    private View view;
    private MainScreenActivity mainScreenActivity;
    private ImageButton imageButtonAddStory;
    private StoryAdapter storyAdapter;
    private PreferenceManager preferenceManager;
    private ArrayList<UserStatus> userStatuses;
    public StoryFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_story, container, false);
        mainScreenActivity = (MainScreenActivity) getActivity();
        assert mainScreenActivity != null;
        preferenceManager = mainScreenActivity.preferenceManager;
//        layoutChoiceImageStory = view.findViewById(R.id.layoutChoiceImage);
//        ImageStoryChoice = view.findViewById(R.id.ImageChoice);
//        imageButtonAddStory = view.findViewById(R.id.addStoryBook);

        imageButtonAddStory = view.findViewById(R.id.addStoryBook);
        imageButtonAddStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogOption();
            }
        });

        getUserStatus();
        initialData();
        return view;
    }


    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void chooseImg() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);

    }

    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }


    private void showDialogOption(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_story_option);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        window.setAttributes(windowAttributes);
        dialog.show();
        dialog.findViewById(R.id.addImageStory).setOnClickListener(v -> {
            chooseImg();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.openCamera).setOnClickListener(v -> {
            //opencamera function
            dialog.dismiss();
        });

    }

    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    assert result.getData() != null;
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getActivity().getApplicationContext().getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        encodedImage = encodeImage(bitmap);
                        Intent intentShowImgStory = new Intent(getContext(), ShowImgStory.class);
                        startActivity(intentShowImgStory);
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
    );

    @SuppressLint("NotifyDataSetChanged")
    private void getUserStatus() {
        userStatuses = new ArrayList<>();

        storyAdapter = new StoryAdapter(mainScreenActivity, userStatuses);
        RecyclerView temp = view.findViewById(R.id.statusList);
        temp.setLayoutManager(new GridLayoutManager(mainScreenActivity, 2));

        temp.setAdapter(storyAdapter);

        ArrayList<Status> statuses = new ArrayList<>();


        statuses.add(new Status(
                "https://firebasestorage.googleapis.com/v0/b/chatsapp-4b8d6.appspot.com/o/status%2F1648451488181?alt=media&token=9e1f4eb7-8825-43c2-8220-106c92409620",
                164845148
        ));
        statuses.add(new Status(
                "https://firebasestorage.googleapis.com/v0/b/chatsapp-4b8d6.appspot.com/o/status%2F1648451488181?alt=media&token=9e1f4eb7-8825-43c2-8220-106c92409620",
                164853774
        ));


        ArrayList<Status> statuses1 = new ArrayList<>();


        statuses1.add(new Status(
                "hhttps://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
                164845148
        ));
        statuses1.add(new Status(
                "https://www.w3schools.com/w3images/fjords.jpg",
                164853774
        ));
        statuses1.add(new Status(
                "https://st.depositphotos.com/1006706/2671/i/600/depositphotos_26715369-stock-photo-which-way-to-choose-3d.jpg",
                164853774
        ));

        ArrayList<Status> statuses2 = new ArrayList<>();


        statuses2.add(new Status(
                "https://www.perma-horti.com/wp-content/uploads/2019/02/image-2.jpg",
                164845148
        ));


        userStatuses.add(new UserStatus(
                "Tuan Anh",
                "https://firebasestorage.googleapis.com/v0/b/chatsapp-4b8d6.appspot.com/o/status%2F1648537741702?alt=media&token=685fb7d8-a05d-429d-9834-1db74b41ff0e",
                164853810,
                statuses
        ));
        userStatuses.add(new UserStatus(
                "TuTuanLe",
                "https://www.w3schools.com/w3images/fjords.jpg",
                174853810,
                statuses1
        ));
        userStatuses.add(new UserStatus(
                "TAnh",
                "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
                184853810,
                statuses2
        ));

        userStatuses.add(new UserStatus(
                "Tram Huynh",
                "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
                9,
                statuses2
        ));
        userStatuses.add(new UserStatus(
                "Phuong",
                "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
                9,
                statuses2
        ));
        userStatuses.add(new UserStatus(
                "Nhon",
                "https://images.unsplash.com/photo-1453728013993-6d66e9c9123a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlld3xlbnwwfHwwfHw%3D&w=1000&q=80",
                9,
                statuses2
        ));



        storyAdapter.notifyDataSetChanged();
    }


    private void initialData() {

        TextView temp = view.findViewById(R.id.nameTextView);
        temp.setText(mainScreenActivity.getTextName());
        RoundedImageView image = view.findViewById(R.id.imageProfile);
        image.setImageBitmap(mainScreenActivity.getBitmap());
    }
}