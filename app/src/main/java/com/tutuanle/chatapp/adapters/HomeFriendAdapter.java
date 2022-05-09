package com.tutuanle.chatapp.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.databinding.ItemContainerUserBinding;
import com.tutuanle.chatapp.interfaces.FriendListener;
import com.tutuanle.chatapp.interfaces.UserListener;
import com.tutuanle.chatapp.models.ChatMessage;
import com.tutuanle.chatapp.models.User;
import com.tutuanle.chatapp.utilities.Constants;

import java.util.ArrayList;
import java.util.List;


public class HomeFriendAdapter extends RecyclerView.Adapter<HomeFriendAdapter.HomeFriendViewHolder> {

    private final List<ChatMessage>chatMessages;
    private final FriendListener friendListener;
    public HomeFriendAdapter(List<ChatMessage> chatMessages, FriendListener friendListener) {
        this.chatMessages = chatMessages;
        this.friendListener = friendListener;
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

        @SuppressLint({"SetTextI18n", "ResourceAsColor"})
        void setData(ChatMessage chatMessage) {
            binding.profile.setImageBitmap(getConversionImage(chatMessage.getConversionImage()));
            binding.username.setText(chatMessage.getConversionName());
            binding.lastMsg.setText(chatMessage.getMessage());
            binding.msgTime.setText(chatMessage.getDateTime());

            Log.d("data_nun",chatMessage.getCountMessageSeen());
            binding.countMessageText.setText(chatMessage.getCountMessageSeen());
            if (!chatMessage.getCountMessageSeen().equals("0")) {
                binding.countMessageFrame.setVisibility(View.VISIBLE);
                binding.username.setTypeface(null, Typeface.BOLD);
                binding.lastMsg.setTypeface(null, Typeface.BOLD);
                binding.lastMsg.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.black));
                binding.username.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.black));

            } else {
                binding.countMessageFrame.setVisibility(View.GONE);
                binding.username.setTypeface(null, Typeface.NORMAL);
                binding.lastMsg.setTypeface(null, Typeface.NORMAL);
                binding.lastMsg.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.colorTextSeen));
                binding.username.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.colorTextSeen));
            }

            binding.getRoot().setOnClickListener(v ->{
                User user = new User();
                user.setUid(chatMessage.getConversionId());
                user.setName(chatMessage.getConversionName());
                user.setProfileImage(chatMessage.getConversionImage());
                friendListener.onFriendClicked(user);
            });
        }
    }

    private Bitmap getConversionImage(String encodedImage)
    {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
