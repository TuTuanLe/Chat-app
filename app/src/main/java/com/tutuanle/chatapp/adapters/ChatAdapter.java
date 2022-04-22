package com.tutuanle.chatapp.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tutuanle.chatapp.databinding.ItemReceiveBinding;
import com.tutuanle.chatapp.databinding.ItemSentBinding;
import com.tutuanle.chatapp.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> chatMessages;
    private final Bitmap receiverProfileImage;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemSentBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else {
            return new ReceiverMessageViewHolder(
                    ItemReceiveBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ( (SentMessageViewHolder) holder).setData(chatMessages.get(position));
        }else{
            ( (ReceiverMessageViewHolder) holder).setData(chatMessages.get(position),receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).getSenderId().equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String senderId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemSentBinding binding;

        SentMessageViewHolder(ItemSentBinding itemSentBinding) {
            super(itemSentBinding.getRoot());
            binding = itemSentBinding;
        }

        public void setData(ChatMessage chatMessage) {
            binding.message.setText(chatMessage.getMessage());
            binding.textDateTime.setText(chatMessage.getDateTime());
        }
    }

    static class ReceiverMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemReceiveBinding binding;

        ReceiverMessageViewHolder(ItemReceiveBinding itemReceiveBinding) {
            super(itemReceiveBinding.getRoot());
            binding = itemReceiveBinding;
        }

        public void setData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
            binding.message.setText(chatMessage.getMessage());
            binding.textDateTime.setText(chatMessage.getDateTime());
            binding.roundedImageView.setImageBitmap(receiverProfileImage);
        }
    }
}
