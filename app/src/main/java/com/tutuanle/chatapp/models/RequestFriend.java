package com.tutuanle.chatapp.models;

public class RequestFriend {
    private String RequestUid;
    private String Image;
    private String Name;
    private String request;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getRequestUid() {
        return RequestUid;
    }

    public void setRequestUid(String requestUid) {
        RequestUid = requestUid;
    }

    public RequestFriend(String requestUid, String image, String name, String request) {
        RequestUid = requestUid;
        Image = image;
        Name = name;
        this.request = request;
    }
}
