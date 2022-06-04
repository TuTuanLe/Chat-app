package com.tutuanle.chatapp.interfaces;

public interface RequestListener {
    void onAcceptFiend(String uid);
    void onCancelRequestFriend(String uid);
    void onShowUser(String uid);
}
