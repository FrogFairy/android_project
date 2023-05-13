package com.example.project;

import java.io.Serializable;

public class Places implements Serializable {
    private long id;
    private String address;
    private float latitude;
    private float longitude;
    private String description;
    private String image;

    public Places (long id, String address, float latitude, float longitude, String description, String image) {
        this.id = id;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() { return image; }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}