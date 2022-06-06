package com.tutuanle.chatapp.models;

import java.util.Date;

public class ChatMessage {
    public String getSenderId() {
        return senderId;
    }

    String Image;

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public  String isActive;

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public  int IsSeen;

    public int getIsSeen() {
        return IsSeen;
    }

    public void setIsSeen(int isSeen) {
        IsSeen = isSeen;
    }

    public Date dataObject;
    private String senderId;
    private String receiverId;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    private String messageId;
    private String message;
    private String dateTime;

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }

    private int feeling =-1;

    private String conversionId, conversionName, conversionImage;

    public String getConversionId() {
        return conversionId;
    }

    public void setConversionId(String conversionId) {
        this.conversionId = conversionId;
    }

    public String getConversionName() {
        return conversionName;
    }

    public void setConversionName(String conversionName) {
        this.conversionName = conversionName;
    }

    public String getConversionImage() {
        return conversionImage;
    }

    public void setConversionImage(String conversionImage) {
        this.conversionImage = conversionImage;
    }

    private  String CountMessageSeen;

    public String getCountMessageSeen() {
        return CountMessageSeen;
    }

    public void setCountMessageSeen(String countMessageSeen) {
        CountMessageSeen = countMessageSeen;
    }

    public int getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(int typeMessage) {
        this.typeMessage = typeMessage;
    }

    public String getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(String imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }

    public String getUrlRecord() {
        return urlRecord;
    }

    public void setUrlRecord(String urlRecord) {
        this.urlRecord = urlRecord;
    }

    private int typeMessage;
    private  String  imageBitmap, urlVideo, urlRecord;
}
