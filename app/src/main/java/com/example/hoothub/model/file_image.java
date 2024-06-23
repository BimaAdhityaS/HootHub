package com.example.hoothub.model;

import com.google.gson.annotations.SerializedName;

public class file_image {

    @SerializedName("Id")
    private String id;
    @SerializedName("Key")
    private String key;

    public file_image(String id, String key) {
        this.id = id;
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
