package com.tutuanle.chatapp.models;

//create model Story to store Story
public class Story {
    public int storyId;
    public int uid;
    public String timeCreate;
    public String img;
    public boolean available;

    public Story(){
    }

    public Story(int uid, String time, String img){
        this.uid = uid;
        this.timeCreate = time;
        this.img = img;
        this.available = true;
    }


    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(String timeUpdate) {
        this.timeCreate = timeUpdate;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }



    /*
        Workflow create story:
            + click button add sotry => new screen show to choose img
            + create Story object ( uid -> user create story id,
                                    timeCreate -> datetime.now(),
                                    img -> convert img to bimap,
                                    available -> true)
            + store object into db

        Test:
            + fetch stories from Story in db with available = true
            + show stories in screen
     */



}
