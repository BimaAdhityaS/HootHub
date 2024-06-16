package com.example.hoothub.model;

public class comment {
    private String id;
    private String user_id;
    private String post_id;
    private String username;
    private String content;
    private String like_count;
    private String reply_count;
    private String created_at;
    private String modified_at;

    @Override
    public String toString() {
        return "comment{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", post_id='" + post_id + '\'' +
                ", username='" + username + '\'' +
                ", content='" + content + '\'' +
                ", like_count='" + like_count + '\'' +
                ", reply_count='" + reply_count + '\'' +
                ", created_at='" + created_at + '\'' +
                ", modified_at='" + modified_at + '\'' +
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

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
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

    public String getReply_count() {
        return reply_count;
    }

    public void setReply_count(String reply_count) {
        this.reply_count = reply_count;
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

    public comment(String id, String user_id, String post_id, String username, String content, String like_count, String reply_count, String created_at, String modified_at) {
        this.id = id;
        this.user_id = user_id;
        this.post_id = post_id;
        this.username = username;
        this.content = content;
        this.like_count = like_count;
        this.reply_count = reply_count;
        this.created_at = created_at;
        this.modified_at = modified_at;
    }
}
