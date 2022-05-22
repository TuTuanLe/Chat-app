package com.tutuanle.chatapp.models;

//create model Story to store Story
public class Story {
    public String storyId;
    public String uid;
    public String timeCreate;
    public String img;
    public boolean available;

    public Story(){

    }
    public Story(String storyId,String uid, String img, String time){
        this.storyId = storyId;
        this.uid = uid;
        this.img = img;
        this.timeCreate = time;
        this.available = true;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(String timeCreate) {
        this.timeCreate = timeCreate;
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
