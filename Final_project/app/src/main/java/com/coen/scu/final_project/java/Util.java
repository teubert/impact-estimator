package com.coen.scu.final_project.java;

import android.util.Log;

import java.util.List;
import java.util.Map;

/**
 * Created by xia on 2/27/18.
 */

public class Util {

    public static String emailToUser(String email) {
        return email.replaceAll("[@.]", "_");
    }


}
