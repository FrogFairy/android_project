package com.example.project;

import java.io.Serializable;

public class Places implements Serializable {
    private String id;
    private String address;
    private float latitude;
    private float longitude;
    private String description;
    private String image;

    public Places (String id, String address, float latitude, float longitude, String description, String image) {
        this.id = id;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.image = image;
    }

    public String getId() {
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