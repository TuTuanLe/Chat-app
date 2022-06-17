package com.tutuanle.chatapp.models;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class YouTube {


    ArrayList<items> items;

    public ArrayList<items> getItems() {
        return items;
    }

    public void setItems(ArrayList<items> items) {
        this.items = items;
    }

    public class items {
        private snippet snippet;

        public snippet getSnippet() {
            return snippet;
        }

        public void setSnippet(snippet snippet) {
            this.snippet = snippet;
        }

        public class snippet {
            String publishedAt;
            String channelId;
            String title;
            String description;
            thumbnails thumbnails;

            public YouTube.items.snippet.thumbnails getThumbnails() {
                return thumbnails;
            }

            public void setThumbnails(YouTube.items.snippet.thumbnails thumbnails) {
                this.thumbnails = thumbnails;
            }

            public String getPublishedAt() {
                return publishedAt;
            }

            public void setPublishedAt(String publishedAt) {
                this.publishedAt = publishedAt;
            }

            public String getChannelId() {
                return channelId;
            }

            public void setChannelId(String channelId) {
                this.channelId = channelId;
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


            public  class thumbnails{
                medium medium;

                public YouTube.items.snippet.thumbnails.medium getMedium() {
                    return medium;
                }

                public void setMedium(YouTube.items.snippet.thumbnails.medium medium) {
                    this.medium = medium;
                }

                public  class medium{
                    String url;
                    int width;
                    int height;

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }

                    public int getWidth() {
                        return width;
                    }

                    public void setWidth(int width) {
                        this.width = width;
                    }

                    public int getHeight() {
                        return height;
                    }

                    public void setHeight(int height) {
                        this.height = height;
                    }
                }
            }
        }
    }
}
