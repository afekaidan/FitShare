package com.example.fitshare.models;

public class Photo {
    private int id;
    private int userId;
    private String userName; // Add this field
    private String imagePath;
    private String caption;
    private long uploadDate;

    // Update constructor
    public Photo(int id, int userId, String imagePath, String caption, long uploadDate) {
        this.id = id;
        this.userId = userId;
        this.imagePath = imagePath;
        this.caption = caption;
        this.uploadDate = uploadDate;
    }

    // Add getter and setter for userName
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public long getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(long uploadDate) {
        this.uploadDate = uploadDate;
    }
}