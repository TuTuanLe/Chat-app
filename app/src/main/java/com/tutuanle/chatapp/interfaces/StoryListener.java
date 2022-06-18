package com.tutuanle.chatapp.interfaces;

public interface StoryListener {
    void OnLikeStory(String uid);
    void OnHeartStory(String uid);
    void OnShowCommentStory(String uid);
}
