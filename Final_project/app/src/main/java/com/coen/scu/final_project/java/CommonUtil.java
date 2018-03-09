package com.coen.scu.final_project.java;

/**
 * Created by xia on 2/27/18.
 */

public class CommonUtil {

    public static String emailToUser(String email) {
        return email.replaceAll("[@.]", "_");
    }


}
