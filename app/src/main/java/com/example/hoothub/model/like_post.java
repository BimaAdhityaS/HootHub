package com.example.hoothub.model;

public class like_post {
    private String id;
    private String user_id;
    private String post_id;
    private String created_at;

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

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public String toString() {
        return "like_post{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", post_id='" + post_id + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }

    public like_post(String id, String user_id, String post_id, String created_at) {
        this.id = id;
        this.user_id = user_id;
        this.post_id = post_id;
        this.created_at = created_at;
    }
}
