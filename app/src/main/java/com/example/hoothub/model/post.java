package com.example.hoothub.model;

import com.google.gson.annotations.SerializedName;

public class post {
    @SerializedName("id")
    private String id;
    @SerializedName("user_id")
    private String  user_id;
    @SerializedName("username")
    private String user_name;
    @SerializedName("content")
    private String content;
    @SerializedName("like_count")
    private String like_count;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("modified_at")
    private String modified_at;
    @SerializedName("comment_count")
    private String comment_count;

    public post(String id, String user_id, String user_name, String content, String like_count, String created_at, String modified_at, String comment_count) {
        this.id = id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.content = content;
        this.like_count = like_count;
        this.created_at = created_at;
        this.modified_at = modified_at;
        this.comment_count = comment_count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "post{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                ", content='" + content + '\'' +
                ", like_count='" + like_count + '\'' +
                ", created_at='" + created_at + '\'' +
                ", modified_at='" + modified_at + '\'' +
                ", comment_count='" + comment_count + '\'' +
                '}';
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLike_count() {
        return like_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getModified_at() {
        return modified_at;
    }

    public void setModified_at(String modified_at) {
        this.modified_at = modified_at;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }
}
