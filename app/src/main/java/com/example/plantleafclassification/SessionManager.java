package com.example.plantleafclassification;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    //variables
    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    private static final String IS_LOGIN = "IsLoggedIn";

    public  static final String KEY_DP = "dp";
    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_USERNAME = "userName";
    public static final String KEY_DOB = "dateOfBirth";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_CONTACT = "contact";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PASSWORD = "password";



    public SessionManager(Context _context) {
        context = _context;
        usersSession = context.getSharedPreferences("userLoginSession", Context.MODE_PRIVATE);
        editor = usersSession.edit();
    }

    public void createLoginSession(String dp, String fullName, String userName, String email, String contact, String dateOfBirth, String address, String password) {

        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_DP, dp);
        editor.putString(KEY_FULLNAME, fullName);
        editor.putString(KEY_USERNAME, userName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_CONTACT, contact);
        editor.putString(KEY_DOB, dateOfBirth);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_PASSWORD, password);

        editor.commit();
    }

    public void setProfileImage(String dp) {
        editor.putString("userImage", dp);
        editor.commit();
    }

    public String getProfileImage() {
        return usersSession.getString("userImage", null);
    }

    public HashMap<String, String> getUsersDetailFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_DP, usersSession.getString(KEY_DP, null));
        userData.put(KEY_FULLNAME, usersSession.getString(KEY_FULLNAME, null));
        userData.put(KEY_USERNAME, usersSession.getString(KEY_USERNAME, null));
        userData.put(KEY_EMAIL, usersSession.getString(KEY_EMAIL, null));
        userData.put(KEY_CONTACT, usersSession.getString(KEY_CONTACT, null));
        userData.put(KEY_DOB, usersSession.getString(KEY_DOB, null));
        userData.put(KEY_ADDRESS, usersSession.getString(KEY_ADDRESS, null));
        userData.put(KEY_PASSWORD, usersSession.getString(KEY_PASSWORD, null));

        return userData;
    }

    public boolean checkLogin() {
        return usersSession.getBoolean(IS_LOGIN, true);
    }

    public void logoutUserFromSession() {
        editor.clear();
        editor.putBoolean(IS_LOGIN, false);
        editor.commit();
    }
}
