package com.tutuanle.chatapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tutuanle.chatapp.R;
import com.tutuanle.chatapp.databinding.ItemReceiveBinding;
import com.tutuanle.chatapp.databinding.ItemSentBinding;
import com.tutuanle.chatapp.interfaces.ChatListener;
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
    private final ChatListener chatListener;

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
                .withPopupColor(Color.WHITE)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (pos > -1) {
                chatListener.showAnimationReaction(pos);
                if (holder.getClass() == SentMessageViewHolder.class) {
                    SentMessageViewHolder viewHolder = (SentMessageViewHolder) holder;
                    viewHolder.binding.feeling.setImageResource(Constants.REACTIONS[pos]);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                    message.setFeeling(pos);
                    updateFeeling(message.getFeeling(), message.getMessageId());

                } else {
                    ReceiverMessageViewHolder viewHolder = (ReceiverMessageViewHolder) holder;
                    viewHolder.binding.feeling.setImageResource(Constants.REACTIONS[pos]);
                    viewHolder.binding.feeling.setVisibility(View.VISIBLE);
                    message.setFeeling(pos);
                    updateFeeling(message.getFeeling(), message.getMessageId());
                }
            }
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
            viewHolder.binding.layoutTypeMessage.setOnLongClickListener((view) -> {
                chatListener.onLongClickRemoveMessage(message.getMessageId(), message.getSenderId());
                popup.onTouch(view, MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                return false;
            });
            viewHolder.binding.messageVideo.setOnClickListener(v ->
                    viewHolder.binding.messageVideo.start()
            );
            if(message.getTypeMessage() == 1){
                viewHolder.binding.layoutTypeMessage.setOnClickListener(v->{
                    chatListener.onClickShowImage(message.getImageBitmap());
                });
            }
            if(message.getTypeMessage() == 6){

                viewHolder.binding.layoutTypeMessage.setOnClickListener(v->{
                    viewHolder.binding.btnAcceptRecord.setImageResource(R.drawable.ic_baseline_pause_24);
                    viewHolder.binding.animationRecord.playAnimation();
                    chatListener.playRecording(message.getUrlRecord());
                });
            }

            if (chatMessages.size() != 0) {
                if (chatMessages.get(chatMessages.size() - 1).getIsSeen() == 1 && chatMessages.get(chatMessages.size() - 1) == message) {
                    viewHolder.binding.checkSeen.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.binding.checkSeen.setVisibility(View.GONE);
                }
            }


        } else if (holder.getClass() == ReceiverMessageViewHolder.class) {
            ReceiverMessageViewHolder viewHolder = (ReceiverMessageViewHolder) holder;
            if (message.getFeeling() >= 0) {
                viewHolder.binding.feeling.setImageResource(Constants.REACTIONS[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.layoutTypeMessage.setOnLongClickListener((view) -> {
                chatListener.onLongClickRemoveMessage(message.getMessageId(), message.getSenderId());
                popup.onTouch(view, MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                return false;
            });
            if(message.getTypeMessage() == 1){
                viewHolder.binding.layoutTypeMessage.setOnClickListener(v->{
                    chatListener.onClickShowImage(message.getImageBitmap());
                });
            }
            if(message.getTypeMessage() == 6){

                viewHolder.binding.layoutTypeMessage.setOnClickListener(v->{
                    viewHolder.binding.btnAcceptRecord.setImageResource(R.drawable.ic_baseline_pause_24);
                    viewHolder.binding.animationRecord.playAnimation();
                    chatListener.playRecording(message.getUrlRecord());
                });
            }
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


    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String senderId, ChatListener chatListener) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
        this.chatListener = chatListener;
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
            if (chatMessage.getTypeMessage() == 0) {
                binding.messageImage.setBackgroundResource(0);
                binding.messageImage.setVisibility(View.GONE);
                binding.messageVideo.setVisibility(View.GONE);
                binding.message.setVisibility(View.VISIBLE);
                binding.unMessage.setVisibility(View.GONE);
                binding.layoutRecording.setVisibility(View.GONE);
            } else if (chatMessage.getTypeMessage() == 1) {
                binding.message.setVisibility(View.GONE);
                binding.messageVideo.setVisibility(View.GONE);
                binding.messageImage.setVisibility(View.VISIBLE);
                binding.messageImage.setImageBitmap(getBitmapFromEnCodedString(chatMessage.getImageBitmap()));
                binding.unMessage.setVisibility(View.GONE);
                binding.layoutRecording.setVisibility(View.GONE);
            } else if (chatMessage.getTypeMessage() == 2) {
                binding.messageImage.setBackgroundResource(0);
                binding.messageImage.setVisibility(View.GONE);
                binding.message.setVisibility(View.GONE);
                binding.messageVideo.setVisibility(View.VISIBLE);
                binding.messageVideo.setVideoURI(Uri.parse("https://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4"));
                binding.messageVideo.requestFocus();
                binding.unMessage.setVisibility(View.GONE);
                binding.layoutRecording.setVisibility(View.GONE);
            }else if(chatMessage.getTypeMessage() == 3){
                binding.unMessage.setVisibility(View.VISIBLE);
                binding.unMessage.setText(chatMessage.getMessage());
                binding.messageImage.setBackgroundResource(0);
                binding.messageImage.setVisibility(View.GONE);
                binding.message.setVisibility(View.GONE);
                binding.messageVideo.setVisibility(View.GONE);
                binding.layoutRecording.setVisibility(View.GONE);
            }else if(chatMessage.getTypeMessage() == 4){
                binding.unMessage.setVisibility(View.VISIBLE);
                binding.unMessage.setText("Remove a message for you. ");
                binding.messageImage.setBackgroundResource(0);
                binding.messageImage.setVisibility(View.GONE);
                binding.message.setVisibility(View.GONE);
                binding.messageVideo.setVisibility(View.GONE);
                binding.layoutRecording.setVisibility(View.GONE);
            }else if(chatMessage.getTypeMessage() == 6) {
                binding.layoutRecording.setVisibility(View.VISIBLE);
                binding.messageImage.setVisibility(View.GONE);
                binding.messageVideo.setVisibility(View.GONE);
                binding.message.setVisibility(View.GONE);
                binding.unMessage.setVisibility(View.GONE);
            }

        }

        private Bitmap getBitmapFromEnCodedString(String enCodedImage) {
            byte[] bytes = Base64.decode(enCodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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
          try  {
              binding.roundedImageView.setImageBitmap(getBitmapFromEnCodedString(chatMessage.getImage()));
          }catch (Exception e){
              Toast.makeText(binding.getRoot().getContext(), "Fail image", Toast.LENGTH_SHORT).show();
          }

            if (chatMessage.getTypeMessage() == 0) {
                binding.messageImage.setBackgroundResource(0);
                binding.messageImage.setVisibility(View.GONE);
                binding.message.setVisibility(View.VISIBLE);
                binding.unMessage.setVisibility(View.GONE);
                binding.layoutRecording.setVisibility(View.GONE);
            } else if (chatMessage.getTypeMessage() == 1) {
                binding.messageImage.setVisibility(View.VISIBLE);
                binding.message.setVisibility(View.GONE);
                binding.unMessage.setVisibility(View.GONE);
                binding.messageImage.setImageBitmap(getBitmapFromEnCodedString(chatMessage.getImageBitmap()));
                binding.layoutRecording.setVisibility(View.GONE);
            } else if (chatMessage.getTypeMessage() == 2) {
                binding.messageImage.setBackgroundResource(0);
                binding.messageImage.setVisibility(View.GONE);
                binding.message.setVisibility(View.GONE);
                binding.unMessage.setVisibility(View.GONE);
                binding.layoutRecording.setVisibility(View.GONE);
            }else if(chatMessage.getTypeMessage() == 3){
                binding.unMessage.setVisibility(View.VISIBLE);
                binding.unMessage.setText(chatMessage.getMessage());
                binding.messageImage.setBackgroundResource(0);
                binding.messageImage.setVisibility(View.GONE);
                binding.message.setVisibility(View.GONE);
                binding.layoutRecording.setVisibility(View.GONE);
            }else if(chatMessage.getTypeMessage() == 4){
                binding.unMessage.setVisibility(View.GONE);
                binding.message.setVisibility(View.VISIBLE);
                binding.message.setText(chatMessage.getMessage());
                binding.messageImage.setBackgroundResource(0);
                binding.messageImage.setVisibility(View.GONE);
                binding.layoutRecording.setVisibility(View.GONE);
            }
            else if(chatMessage.getTypeMessage() == 5){
                binding.unMessage.setVisibility(View.VISIBLE);
                binding.message.setVisibility(View.GONE);
                binding.unMessage.setText("Remove a message for you.");
                binding.messageImage.setBackgroundResource(0);
                binding.messageImage.setVisibility(View.GONE);
                binding.layoutRecording.setVisibility(View.GONE);
            }else if(chatMessage.getTypeMessage() == 6){
                binding.layoutRecording.setVisibility(View.VISIBLE);
                binding.unMessage.setVisibility(View.GONE);
                binding.messageImage.setVisibility(View.GONE);
                binding.message.setVisibility(View.GONE);
            }
        }

        private Bitmap getBitmapFromEnCodedString(String enCodedImage) {
            byte[] bytes = Base64.decode(enCodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }

    }
}
