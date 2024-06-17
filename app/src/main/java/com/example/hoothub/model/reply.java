package com.example.hoothub.model;

public class reply {
    private String id;
    private String user_id;
    private String comment_id;
    private String username;
    private String content;

    @Override
    public String toString() {
        return "reply{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", comment_id='" + comment_id + '\'' +
                ", username='" + username + '\'' +
                ", content='" + content + '\'' +
                ", like_count='" + like_count + '\'' +
                ", created_at='" + created_at + '\'' +
                ", modified_at='" + modified_at + '\'' +
                '}';
    }

    public reply(String id, String user_id, String comment_id, String username, String content, String like_count, String reply_count, String created_at, String modified_at) {
        this.id = id;
        this.user_id = user_id;
        this.comment_id = comment_id;
        this.username = username;
        this.content = content;
        this.like_count = like_count;
        this.created_at = created_at;
        this.modified_at = modified_at;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    private String like_count;
    private String created_at;
    private String modified_at;
}
