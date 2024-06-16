package com.example.hoothub.lib;

public class Post {
    private String name, post_content, time;
    private int img, post_like, post_comment;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPost_like() {
        return post_like;
    }

    public void setPost_like(int post_like) {
        this.post_like = post_like;
    }

    public int getPost_comment() {
        return post_comment;
    }

    public void setPost_comment(int post_comment) {
        this.post_comment = post_comment;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
