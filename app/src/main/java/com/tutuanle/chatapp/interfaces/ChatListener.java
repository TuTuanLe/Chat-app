package com.tutuanle.chatapp.interfaces;

public interface ChatListener {
    void onClickShowImage(String url);
    void onLongClickRemoveMessage(String uidMessage, String senderId);
    void showAnimationReaction(int index);
    void playRecording(String url);
    void onClickShowVideo(String url);
    void onClickShowVideoYouTobe(String url);

}
