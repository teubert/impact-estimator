package com.coen.scu.final_project.java;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qigao on 07/03/2018.
 */

public class FirebaseUtils {
    public static DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static void addUserToFirebase(FirebaseUser user, String email) {
        DatabaseReference database = getDatabase();
        String userId = user.getUid();
        database.child("userList").child(CommonUtil.emailToUser(email)).setValue(userId);
        database.child("idEmailMap").child(userId).setValue(email);
        Map<String, List<String>> friendMap = new HashMap<>();
        database.child("friendMap").child(userId).setValue(friendMap);
    }
}
