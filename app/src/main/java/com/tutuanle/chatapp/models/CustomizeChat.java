package com.tutuanle.chatapp.models;

public class CustomizeChat {
    String CustomizeUid;
    String UserUid_1;
    String UserUid_2;
    int theme;
    String gradient;

    public CustomizeChat() {

    }

    public String getCustomizeUid() {
        return CustomizeUid;
    }

    public void setCustomizeUid(String customizeUid) {
        CustomizeUid = customizeUid;
    }

    public String getUserUid_1() {
        return UserUid_1;
    }

    public void setUserUid_1(String userUid_1) {
        UserUid_1 = userUid_1;
    }

    public String getUserUid_2() {
        return UserUid_2;
    }

    public void setUserUid_2(String userUid_2) {
        UserUid_2 = userUid_2;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public String getGradient() {
        return gradient;
    }

    public void setGradient(String gradient) {
        this.gradient = gradient;
    }
}
