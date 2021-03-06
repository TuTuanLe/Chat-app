package com.tutuanle.chatapp.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;

import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.LightsLoader;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.activities.MainScreenActivity;
import com.tutuanle.chatapp.activities.ProfileActivity;
import com.tutuanle.chatapp.adapters.CommentAdapter;
import com.tutuanle.chatapp.adapters.StoryAdapter;
import com.tutuanle.chatapp.interfaces.StoryListener;
import com.tutuanle.chatapp.models.ChatMessage;
import com.tutuanle.chatapp.models.Comment;
import com.tutuanle.chatapp.models.InformationYouTube;
import com.tutuanle.chatapp.models.Status;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.models.UserStatus;
import com.tutuanle.chatapp.utilities.Constants;
import com.tutuanle.chatapp.utilities.PreferenceManager;
import com.yqritc.scalablevideoview.ScaleManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class StoryFragment extends Fragment implements StoryListener {


    static class SortByRoll implements Comparator<UserStatus> {
        public int compare(UserStatus a, UserStatus b) {
            return (int) b.getLastUpdated() - (int) a.getLastUpdated();
        }
    }

    static class SortByTimeSpan implements Comparator<Status> {
        public int compare(Status a, Status b) {
            return (int) b.getTimeStamp() - (int) a.getTimeStamp();
        }
    }

    private View view;
    private MainScreenActivity mainScreenActivity;

    private StoryAdapter storyAdapter;
    private CommentAdapter commentAdapter;
    private PreferenceManager preferenceManager;
    private ArrayList<UserStatus> userStatuses;
    FirebaseFirestore database;
    ProgressDialog progressDialog;

    int check = 0;

    private ArrayList<Comment> comments;

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
        loading(true);
        getUserStatus();
        initialData();
        setOnClickListener();
        getOnStoriesListener();
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getUserStatus() {
        userStatuses = new ArrayList<>();
        storyAdapter = new StoryAdapter(mainScreenActivity, userStatuses, this);
        RecyclerView temp = view.findViewById(R.id.statusList);
        temp.setAdapter(storyAdapter);
    }


    private void initialData() {

        TextView temp = view.findViewById(R.id.nameTextView);
        temp.setText(mainScreenActivity.getTextName());
        RoundedImageView image = view.findViewById(R.id.imageProfile);
        image.setImageBitmap(mainScreenActivity.getBitmap());
        progressDialog = new ProgressDialog(mainScreenActivity);
        progressDialog.setMessage("upLoading Image ...");
        progressDialog.setCancelable(false);
        database = FirebaseFirestore.getInstance();
        image.setOnClickListener(v -> {
            User usermod = new User();
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            usermod.setName(mainScreenActivity.preferenceManager.getString(Constants.KEY_NAME));
            usermod.setUid(mainScreenActivity.preferenceManager.getString(Constants.KEY_USER_ID));
            usermod.setProfileImage(mainScreenActivity.preferenceManager.getString(Constants.KEY_IMAGE));
            usermod.setPhoneNumber(mainScreenActivity.preferenceManager.getString(Constants.KEY_NUMBER_PHONE));
            usermod.setPassword(mainScreenActivity.preferenceManager.getString(Constants.KEY_PASSWORD));
            usermod.setEmail(mainScreenActivity.preferenceManager.getString(Constants.KEY_EMAIL));
            intent.putExtra(Constants.KEY_USER, usermod);
            startActivity(intent);
        });
    }

    private void setOnClickListener() {
        view.findViewById(R.id.addStoryBook).setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 2);
        });
    }

    synchronized void getOnStoriesListener() {
        database.collection(Constants.KEY_COLLECTION_STORIES)
                .addSnapshotListener(eventListener);

    }

    @SuppressLint("NotifyDataSetChanged")
    void getStatuesListener() {

        for (int i = 0; i < userStatuses.size(); i++) {
            int b = i;
            database.collection(Constants.KEY_COLLECTION_STORIES)
                    .document(userStatuses.get(i).getStatusUid())
                    .collection(Constants.KEY_COLLECTION_STATUSES)
                    .addSnapshotListener((valueStatuses, errorStatuses) -> {
                        if (errorStatuses != null) {
                            return;
                        }
                        if (valueStatuses != null) {
                            ArrayList<Status> statuses = new ArrayList<>();
                            for (DocumentChange documentChangeStatus : valueStatuses.getDocumentChanges()) {
                                if (documentChangeStatus.getType() == DocumentChange.Type.ADDED) {
                                    Status statusTemp = new Status();
                                    statusTemp.setImageUrl(documentChangeStatus.getDocument().getString("imageUrl"));
                                    statusTemp.setTimeStamp(documentChangeStatus.getDocument().getLong("timeStamp"));
                                    statuses.add(statusTemp);
                                }
                            }
                            Collections.sort(statuses, new SortByTimeSpan());
                            userStatuses.get(b).setStatuses(statuses);
                            Collections.sort(userStatuses, new SortByRoll());

                        }
                    });


        }


    }


    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
//            userStatuses.clear();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    UserStatus status = new UserStatus();
                    status.setStatusUid(documentChange.getDocument().getId());
                    status.setName(documentChange.getDocument().getString(Constants.KEY_NAME));
                    status.setProfileImage(documentChange.getDocument().getString(Constants.KEY_IMAGE));
                    status.setLastUpdated(documentChange.getDocument().getLong(Constants.KEY_LAST_UPDATE));
                    status.setCaption(documentChange.getDocument().getString(Constants.KEY_CAPTION));
                    userStatuses.add(status);
                }
                if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    int index = findIndexStory(documentChange.getDocument().getId());
                    userStatuses.get(index).setCaption(documentChange.getDocument().getString(Constants.KEY_CAPTION));
                    userStatuses.get(index).setLastUpdated(documentChange.getDocument().getLong(Constants.KEY_LAST_UPDATE));
                    userStatuses.get(index).setProfileImage(documentChange.getDocument().getString(Constants.KEY_IMAGE));
                }
            }
            Collections.sort(userStatuses, new SortByRoll());


            getStatuesListener();


            storyAdapter.notifyDataSetChanged();

            RecyclerView temp = view.findViewById(R.id.statusList);


            new Handler().postDelayed(() -> {
                {
                    if (checkNUllData() == 1) {
                        temp.setAdapter(storyAdapter);
                        temp.setVisibility(View.VISIBLE);
                        loading(false);
                    } else {
                        new Handler().postDelayed(() -> {
                            {
                                if (checkNUllData() == 1) {
                                    temp.setAdapter(storyAdapter);
                                    temp.setVisibility(View.VISIBLE);
                                    loading(false);
                                } else {
                                    new Handler().postDelayed(() -> {
                                        {
                                            if (checkNUllData() == 1) {
                                                temp.setAdapter(storyAdapter);
                                                temp.setVisibility(View.VISIBLE);
                                                loading(false);
                                            } else {
                                                new Handler().postDelayed(() -> {
                                                    {
                                                        if (checkNUllData() == 1) {
                                                            temp.setAdapter(storyAdapter);
                                                            temp.setVisibility(View.VISIBLE);
                                                            loading(false);
                                                        } else {
                                                            Toast.makeText(mainScreenActivity, "please refresh screen ...", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }, 1000);
                                            }
                                        }
                                    }, 500);
                                }
                            }
                        }, 300);
                    }
                }
            }, 200);


            if (check == 1) {
                new Handler().postDelayed(() -> {
                    {
                        if (checkNUllData() == 1) {
                            temp.setAdapter(storyAdapter);
                            temp.setVisibility(View.VISIBLE);
                            loading(false);
                        } else {
                            check = 0;
                        }
                        Log.e("TIMEOUT__: ", "1000");

                    }
                }, 1000);

            }

            if (check == 1) {
                new Handler().postDelayed(() -> {
                    {
                        if (checkNUllData() == 1) {
                            temp.setAdapter(storyAdapter);
                            temp.setVisibility(View.VISIBLE);
                            loading(false);
                        } else {
                            check = 0;
                        }
                        Log.e("TIMEOUT__: ", "1000");

                    }
                }, 2000);

            }

        }
    };

    private int checkNUllData() {
        for (int i = 0; i < userStatuses.size(); i++) {
            try {
                for (int j = 0; j < userStatuses.get(i).getStatuses().size(); j++) {
                    if (userStatuses.get(i).getStatuses() == null) {
                        return 0;
                    }
                }
            } catch (Exception e) {
                return 0;
            }
        }

        return 1;
    }


    private int findIndexStory(String uid) {
        for (int i = 0; i < userStatuses.size(); i++)
            if (userStatuses.get(i).getStatusUid().equals(uid))
                return i;
        return -1;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == -1 && data != null) {
            openDialogBottom(data.getData());
        }
    }


    private void openDialogBottom(Uri uriStory) {
        final Dialog dialog = new Dialog(mainScreenActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_story);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.FILL_VERTICAL;
        window.setAttributes(windowAttributes);
        TextView textView = dialog.findViewById(R.id.textCaption);
        ImageView imageView = dialog.findViewById(R.id.imageChoice);
        imageView.setImageURI(uriStory);
        imageView.setOnClickListener(v -> {
            textView.onEditorAction(EditorInfo.IME_ACTION_DONE);
        });
        dialog.findViewById(R.id.sendStory).setOnClickListener(v ->
                {
                    dialog.dismiss();
                    progressDialog.show();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    Date date = new Date();
                    StorageReference reference = storage.getReference().child("status").child(date.getTime() + "");
                    reference.putFile(uriStory).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                UserStatus userStatus = new UserStatus();
                                userStatus.setName(preferenceManager.getString(Constants.KEY_NAME));
                                userStatus.setProfileImage(preferenceManager.getString(Constants.KEY_IMAGE));
                                userStatus.setLastUpdated(date.getTime());
                                userStatus.setCaption(textView.getText().toString());
                                HashMap<String, Object> obj = new HashMap<>();
                                obj.put(Constants.KEY_NAME, userStatus.getName());
                                obj.put(Constants.KEY_IMAGE, userStatus.getProfileImage());
                                obj.put(Constants.KEY_LAST_UPDATE, userStatus.getLastUpdated());
                                obj.put(Constants.KEY_CAPTION, userStatus.getCaption());
                                String imageUrl = uri.toString();
                                Status status = new Status(imageUrl, userStatus.getLastUpdated());
                                database.collection(Constants.KEY_COLLECTION_STORIES)
                                        .document(preferenceManager.getString(Constants.KEY_USER_ID))
                                        .collection(Constants.KEY_COLLECTION_STATUSES)
                                        .add(status);
                                database.collection(Constants.KEY_COLLECTION_STORIES)
                                        .document(preferenceManager.getString(Constants.KEY_USER_ID))
                                        .set(obj);
                                progressDialog.dismiss();
                            });
                        }
                    });
                }
        );

        dialog.findViewById(R.id.imageClose).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.setCancelable(true);
        dialog.show();
    }

    private void loading(Boolean isLoading) {
        LightsLoader temp = view.findViewById(R.id.progress);
        if (isLoading) {
            temp.setVisibility(View.VISIBLE);
        } else {
            temp.setVisibility(View.INVISIBLE);
        }
    }

    private void openDialogCenter() {
        final Dialog dialog = new Dialog(mainScreenActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_comment);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        window.setAttributes(windowAttributes);

        RoundedImageView roundedImageView = dialog.findViewById(R.id.profile);
        roundedImageView.setImageBitmap(getBitmapFromEnCodedString(preferenceManager.getString(Constants.KEY_IMAGE)));
//        profile
        dialog.setCancelable(true);

        dialog.findViewById(R.id.sendComment).setOnClickListener(v -> {
            EditText editText = dialog.findViewById(R.id.editText);
            addComment(editText.getText().toString());
            dialog.dismiss();
        });
        ProgressBar progressBar = dialog.findViewById(R.id.progressBarComment);
        progressBar.setVisibility(View.VISIBLE);


        RecyclerView recyclerView = dialog.findViewById(R.id.listComment);
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(mainScreenActivity, comments);
        showCommentStories();
        recyclerView.setVisibility(View.VISIBLE);
        TextView tv = dialog.findViewById(R.id.noComment);
        TextView countComment = dialog.findViewById(R.id.countComment);

        new Handler().postDelayed(() -> {
            if (comments.size() == 0) {
//                recyclerView.smoothScrollToPosition(comments.size() - 1);
                tv.setVisibility(View.VISIBLE);
                countComment.setText("0");

            } else {
                recyclerView.smoothScrollToPosition(comments.size() - 1);
                tv.setVisibility(View.GONE);
                countComment.setText(String.format("%d", comments.size()));
            }

            recyclerView.setAdapter(commentAdapter);
            progressBar.setVisibility(View.GONE);
        }, 500);


        dialog.findViewById(R.id.icon_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private String uidStory;

    private void addComment(String message) {
        HashMap<String, Object> cm = new HashMap<>();
        cm.put("uid", uidStory);
        cm.put("name", preferenceManager.getString(Constants.KEY_NAME));
        cm.put("message", message);
        cm.put("timestamp", new Date());
        cm.put("image", preferenceManager.getString(Constants.KEY_IMAGE));
        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_COMMENT).add(cm);
    }

    private void showCommentStories() {
        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_COMMENT)
                .whereEqualTo("uid", uidStory)
                .addSnapshotListener(eventCommentListener);
    }


    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventCommentListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {

                Comment comment = new Comment();
                comment.setUid(documentChange.getDocument().getString("uid"));
                comment.setName(documentChange.getDocument().getString("name"));
                comment.setImage(documentChange.getDocument().getString("image"));
                comment.setTimestamp(getReadableDatetime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP)));
                comment.setMessage(documentChange.getDocument().getString("message"));
                comment.dataObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                comments.add(comment);
                Log.d("TEST_DATA__", ": " + documentChange.getDocument().getString("message"));


            }
            Collections.sort(comments, (x, y) -> y.dataObject.compareTo(x.dataObject));

            commentAdapter.notifyDataSetChanged();
        }
    };

    private String getReadableDatetime(Date date) {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);
    }

    private Bitmap getBitmapFromEnCodedString(String enCodedImage) {
        byte[] bytes = Base64.decode(enCodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public void OnLikeStory(String uid) {
        uidStory = uid;
        Toast.makeText(mainScreenActivity, "like", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void OnHeartStory(String uid) {
        uidStory = uid;
        Toast.makeText(mainScreenActivity, "heart", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void OnShowCommentStory(String uid) {
        uidStory = uid;
        openDialogCenter();
        Log.d("TEST_DATA__", ": " + uidStory);

    }


}