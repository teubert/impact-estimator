package com.coen.scu.final_project.java;

/**
 * Created by xia on 3/6/18.
 */

public class RankingUser {
    private String mName;
    private String mEmission;
    private String mImageUrl;

    public RankingUser(){

    }

    public RankingUser(String name, String emission, String image){
       mName = name;
       mEmission = emission;
       mImageUrl = image;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmEmission() {
        return mEmission;
    }

    public void setmEmission(String mEmission) {
        this.mEmission = mEmission;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
