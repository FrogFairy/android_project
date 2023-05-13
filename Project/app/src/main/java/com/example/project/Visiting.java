package com.example.project;

import java.io.Serializable;

public class Visiting implements Serializable {
    private long id;
    private long user_id;
    private long place_id;
    private String images;

    public Visiting (long id, long user_id, long place_id, String images) {
        this.id = id;
        this.user_id = user_id;
        this.place_id = place_id;
        this.images = images;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return user_id;
    }

    public long getPlaceId() {
        return place_id;
    }

    public String getImages() {
        return images;
    }
}