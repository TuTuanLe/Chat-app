package com.tutuanle.chatapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tutuanle.chatapp.databinding.ItemContainerUserBinding;
import com.tutuanle.chatapp.interfaces.UserListener;
import com.tutuanle.chatapp.models.ChatMessage;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;

import java.util.ArrayList;
import java.util.List;


public class HomeFriendAdapter extends RecyclerView.Adapter<HomeFriendAdapter.HomeFriendViewHolder> {

    private final List<ChatMessage>chatMessages;
    private final UserListener userListener;
    public HomeFriendAdapter(List<ChatMessage> chatMessages, UserListener userListener) {
        this.chatMessages = chatMessages;
        this.userListener = userListener;
    }


    @NonNull
    @Override
    public HomeFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeFriendViewHolder(
                ItemContainerUserBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull HomeFriendViewHolder holder, int position) {
            holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }



    class HomeFriendViewHolder extends RecyclerView.ViewHolder{

        ItemContainerUserBinding binding;
        HomeFriendViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding= itemContainerUserBinding;

        }

        void setData(ChatMessage chatMessage){
            binding.profile.setImageBitmap(getConversionImage(chatMessage.getConversionImage()));
            binding.username.setText(chatMessage.getConversionName());
            binding.lastMsg.setText(chatMessage.getMessage());
            binding.msgTime.setText(chatMessage.getDateTime());

            User user = new User();
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.KEY_COLLECTION_USERS)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                if (chatMessage.getReceiverId().equals(queryDocumentSnapshot.getId())) {
                                    user.setName(queryDocumentSnapshot.getString(Constants.KEY_NAME));
                                    user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_EMAIL));
                                    user.setProfileImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE));
                                    user.setToken(queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN));
                                    user.setUid(queryDocumentSnapshot.getId());
                                    break;
                                }
                                if (chatMessage.getSenderId().equals(queryDocumentSnapshot.getId())) {
                                    user.setName(queryDocumentSnapshot.getString(Constants.KEY_NAME));
                                    user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_EMAIL));
                                    user.setProfileImage(queryDocumentSnapshot.getString(Constants.KEY_IMAGE));
                                    user.setToken(queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN));
                                    user.setUid(queryDocumentSnapshot.getId());
                                    break;
                                }
                            }

                        }
                    });
            binding.getRoot().setOnClickListener(v ->userListener.onUserClicked(user));
        }
    }

    private Bitmap getConversionImage(String encodedImage)
    {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
