package com.example.hoothub.model;

public class report {
    private String id;
    private String user_id;
    private String post_id;
    private String reply_id;
    private String comment_id;
    private String reason;
    private String create_at;
    @Override
    public String toString() {
        return "report{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", post_id='" + post_id + '\'' +
                ", reply_id='" + reply_id + '\'' +
                ", comment_id='" + comment_id + '\'' +
                ", reason='" + reason + '\'' +
                ", create_at='" + create_at + '\'' +
                '}';
    }

    public report(String id, String user_id, String post_id, String reply_id, String comment_id, String reason, String create_at) {
        this.id = id;
        this.user_id = user_id;
        this.post_id = post_id;
        this.reply_id = reply_id;
        this.comment_id = comment_id;
        this.reason = reason;
        this.create_at = create_at;
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

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }
}
