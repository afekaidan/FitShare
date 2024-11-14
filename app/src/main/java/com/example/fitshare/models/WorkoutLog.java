package com.example.fitshare.models;

import java.util.Date;

public class WorkoutLog {
    private int id;
    private int userId;
    private String userName;
    private String workoutType;
    private String description;
    private int duration;
    private Date date;

    public WorkoutLog(int id, int userId, String workoutType, String description, int duration) {
        this.id = id;
        this.userId = userId;
        this.workoutType = workoutType;
        this.description = description;
        this.duration = duration;
        this.date = new Date();
    }

    // Constructor with userName for feed display
    public WorkoutLog(int id, int userId, String userName, String workoutType,
                      String description, int duration, Date date) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.workoutType = workoutType;
        this.description = description;
        this.duration = duration;
        this.date = date;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
