package com.tutuanle.chatapp.interfaces;

import com.tutuanle.chatapp.models.User;

public interface UserListener {
    void onUserClicked(User user);
    void initialVideoMeeting(User user);
    void initialAudioMeeting(User user);
}
