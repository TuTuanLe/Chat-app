package com.tutuanle.chatapp.models;

public class InformationYouTube {
    String url;
    String urlImage;
    String title;
    String description;
    String videoId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    @Override
    public String toString() {
        return "InformationYouTube{" +
                "url='" + url + '\'' +
                ", urlImage='" + urlImage + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", videoId='" + videoId + '\'' +
                '}';
    }
}
