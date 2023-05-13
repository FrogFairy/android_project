package com.example.project;

import java.io.Serializable;

public class Users implements Serializable {
    private long id;
    private String username;

    public Users (long id, String username) {
        this.id = id;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}