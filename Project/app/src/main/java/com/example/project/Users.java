package com.example.project;

import java.io.Serializable;

public class Users implements Serializable {
    private String id;
    private String username;

    public Users (String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}