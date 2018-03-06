package com.coen.scu.final_project.java;

/**
 * Created by xia on 3/6/18.
 */

public class FriendUser {
   private String mId;
   private String mDate;

   public FriendUser (){

   }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public FriendUser(String id, String date) {
       mId = id;
       mDate = date;
   }
}
