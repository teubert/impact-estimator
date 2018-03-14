package com.coen.scu.final_project.java;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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

    public static void addUserToFirebase(FirebaseUser user){
        DatabaseReference database = getDatabase();
        String userId = user.getUid();
        String userName = user.getDisplayName() == null || user.getDisplayName().equals("")
                ? user.getDisplayName() : "Unnamed User";
        database.child("idEmailMap").child(userId).setValue(user.getEmail());
        Map<String, List<String>> friendMap = new HashMap<>();
        database.child("friendMap").child(userId).setValue(friendMap);
        database.child("users").child(userId).child("name").setValue(userName);
        database.child("users").child(userId).child("email").setValue(user.getEmail());
        database.child("users").child(userId).child("car_type").setValue("UNKOWN");
        database.child("users").child(userId).child("diet_type").setValue("Balanced");
        String url = "https://firebasestorage.googleapis.com/v0/b/android-final-project-471bd.appspot.com/o/Portrait%2Fdefault.png?alt=media&token=922d687a-6c4a-4b08-940b-303b570e6894";
        database.child("users").child(userId).child("image").setValue(url);
    }
}
