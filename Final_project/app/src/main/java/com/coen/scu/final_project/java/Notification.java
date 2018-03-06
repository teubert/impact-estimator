package com.coen.scu.final_project.java;

/**
 * Created by xia on 3/5/18.
 */

public class Notification {

    private String mFromId;
    private String mUserName;
    private String mMessage;


    public Notification() {
    }

    public String getmFromId() {
        return mFromId;
    }

    public void setmFromId(String mFromId) {
        this.mFromId = mFromId;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public Notification(String fromId, String username) {
        mFromId = fromId;
        mUserName = username;
        mMessage = username + " Sent Your a Friend Request";
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }
}

