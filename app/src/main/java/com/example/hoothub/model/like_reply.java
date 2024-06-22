package com.example.hoothub.model;

public class like_reply {
    private String id;
    private String user_id;
    private String reply_id;
    private String created_at;
    @Override
    public String toString() {
        return "like_reply{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", reply_id='" + reply_id + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }

    public like_reply(String id, String user_id, String reply_id, String created_at) {
        this.id = id;
        this.user_id = user_id;
        this.reply_id = reply_id;
        this.created_at = created_at;
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

    public String getReply_id() {
        return reply_id;
    }

    public void setReply_id(String reply_id) {
        this.reply_id = reply_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
