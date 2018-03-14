package com.coen.scu.final_project.java;

/**
 * Created by xia on 3/6/18.
 */

public class RankingUser {
    private String mName;
    private Double mEmission;
    private String mImageUrl;
    private String mId;

    public RankingUser(){

    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public RankingUser(String name, Double emission, String image, String id){
       mName = name;
       mEmission = emission;
       mImageUrl = image;
       mId = id;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public Double getmEmission() {
        return mEmission;
    }

    public void  setmEmission(Double mEmission) {
        this.mEmission = mEmission;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
