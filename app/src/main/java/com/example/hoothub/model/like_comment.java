package com.example.hoothub.model;

public class like_comment {
    private String id;
    private String user_id;
    private String comment_id;
    private String created_at;

    public like_comment(String id, String user_id, String comment_id, String created_at) {
        this.id = id;
        this.user_id = user_id;
        this.comment_id = comment_id;
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "like_comment{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", comment_id='" + comment_id + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
