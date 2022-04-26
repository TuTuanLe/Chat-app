package com.tutuanle.chatapp.adapters;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tutuanle.chatapp.databinding.ItemReceiveBinding;
import com.tutuanle.chatapp.databinding.ItemSentBinding;
import com.tutuanle.chatapp.models.ChatMessage;
import com.tutuanle.chatapp.utilities.Constants;


import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> chatMessages;
    private final Bitmap receiverProfileImage;
    private final String senderId;
    private Context context;
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        ChatMessage message = chatMessages.get(position);

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(Constants.REACTIONS)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (holder.getClass() == SentMessageViewHolder.class) {
                SentMessageViewHolder viewHolder = (SentMessageViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(Constants.REACTIONS[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);

            } else {
                ReceiverMessageViewHolder viewHolder = (ReceiverMessageViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(Constants.REACTIONS[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);

            }
            message.setFeeling(pos);
            updateFeeling(message.getFeeling(), message.getMessageId());
            return true;
        });


        if (holder.getClass() == SentMessageViewHolder.class) {
            SentMessageViewHolder viewHolder = (SentMessageViewHolder) holder;
            if (message.getFeeling() >= 0) {
                viewHolder.binding.feeling.setImageResource(Constants.REACTIONS[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }
            viewHolder.binding.message.setOnTouchListener((view, motionEvent) -> {
                popup.onTouch(view, motionEvent);
                return false;
            });
        } else if (holder.getClass() == ReceiverMessageViewHolder.class) {
            ReceiverMessageViewHolder viewHolder = (ReceiverMessageViewHolder) holder;
            if (message.getFeeling() >= 0) {
                viewHolder.binding.feeling.setImageResource(Constants.REACTIONS[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener((view, motionEvent) -> {
                popup.onTouch(view, motionEvent);
                return false;
            });
        }

        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            assert holder instanceof SentMessageViewHolder;
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        } else {
            assert holder instanceof ReceiverMessageViewHolder;
            ((ReceiverMessageViewHolder) holder).setData(chatMessages.get(position), receiverProfileImage);
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


    private void updateFeeling(int feeling, String messageId) {

        FirebaseFirestore.getInstance()
                .collection(Constants.KEY_COLLECTION_CHAT)
                .document(messageId)
                .update(Constants.KEY_FEELING, feeling)
                .addOnSuccessListener(item -> Log.d("FEELING", "update successfully"))
                .addOnFailureListener(item -> Log.d("FEELING", "fail "));
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
