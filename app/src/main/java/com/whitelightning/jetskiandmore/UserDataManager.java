package com.whitelightning.jetskiandmore;

import android.content.Context;
import android.content.SharedPreferences;

public class UserDataManager {

    private final Context context;

    public UserDataManager(Context context) {
        this.context = context;
    }

    public void saveUserData(String username, String email, String phoneNumber, String password) {
        SharedPreferences preferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_name", username);
        editor.putString("user_email", email);
        editor.putString("Phone number", phoneNumber);
        editor.putString("Password", password);
        editor.apply();
    }

    public String getUserName() {
        SharedPreferences preferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        return preferences.getString("user_name", "John Doe");
    }
}


