package com.example.hoothub.model;

public class post {
    private String id;
    private String  user_id;
    private String content;
    private String like_count;
    private String created_at;
    private String modified_at;
    private String comment_count;

    @Override
    public String toString() {
        return "post{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", content='" + content + '\'' +
                ", like_count='" + like_count + '\'' +
                ", created_at='" + created_at + '\'' +
                ", modified_at='" + modified_at + '\'' +
                ", comment_count='" + comment_count + '\'' +
                '}';
    }

    public post(String id, String user_id, String content, String like_count, String created_at, String modified_at, String comment_count) {
        this.id = id;
        this.user_id = user_id;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
