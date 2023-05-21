package com.example.project;

import java.io.Serializable;

public class Visiting implements Serializable {
    private String id;
    private String user_id;
    private String place_id;
    private String images;

    public Visiting (String id, String user_id, String place_id, String images) {
        this.id = id;
        this.user_id = user_id;
        this.place_id = place_id;
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return user_id;
    }

    public String getPlaceId() {
        return place_id;
    }

    public String getImages() {
        return images;
    }
}