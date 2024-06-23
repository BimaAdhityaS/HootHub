package com.example.hoothub.model;

import com.google.gson.annotations.SerializedName;

public class user {
    @SerializedName("id")
    private String id;

    @SerializedName("img_profile")
    private String img_profile;
    @SerializedName("first name")
    private String firstName;
    @SerializedName("last name")
    private String lastName;
    @SerializedName("username")
    private String username;
    @SerializedName("bio")
    private String bio;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("modified_at")
    private String modifiedAt;

    @Override
    public String toString() {
        return "user{" +
                "id=" + id +
                ", img_url='" + img_profile + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", bio='" + bio + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", modifiedAt='" + modifiedAt + '\'' +
                '}';
    }

    public user(String id, String img_url, String firstName, String lastName, String username, String bio, String email, String password, String createdAt, String modifiedAt) {
        this.id = id;
        this.img_profile = img_url;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.bio = bio;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getImg_profile() {
        return img_profile;
    }

    public void setImg_profile(String img_url) {
        this.img_profile = img_url;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
