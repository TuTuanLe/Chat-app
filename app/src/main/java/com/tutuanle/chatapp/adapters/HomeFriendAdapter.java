package com.tutuanle.chatapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tutuanle.chatapp.databinding.ItemContainerUserBinding;
import com.tutuanle.chatapp.models.ChatMessage;

import java.util.List;


public class HomeFriendAdapter extends RecyclerView.Adapter<HomeFriendAdapter.HomeFriendViewHolder> {

    private final List<ChatMessage>chatMessages;

    public HomeFriendAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
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
        }
    }

    private Bitmap getConversionImage(String encodedImage)
    {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
