package com.android.project.firechat.activities;


public class Contact {
    private String userName;
    private String userId;
    private String status;

    public Contact(String userName, String userId) {
        this.userName = userName;
        this.userId = userId;
    }

    public Contact(String userName, String userId, String status){
        this.userName = userName;
        this.userId = userId;
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
