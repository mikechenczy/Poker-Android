package com.mj.poker.util;

import android.content.SharedPreferences;

import com.mj.poker.Const;
import com.mj.poker.ui.LoginActivity;
import com.mj.poker.ui.RoomsActivity;

import static android.content.Context.MODE_PRIVATE;

public class DataUtil {
    public static SharedPreferences sharedPreferences = LoginActivity.INSTANCE==null?
            RoomsActivity.INSTANCE.getSharedPreferences("yan",MODE_PRIVATE) : LoginActivity.INSTANCE.getSharedPreferences("yan",MODE_PRIVATE);
    public static void save() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("agree", Const.agree);
        if(Const.user!=null) {
            editor.putString("username", Const.user.getUsername());
            if(Const.autoLogin) {
                editor.putBoolean("autoLogin", true);
                editor.putString("password", Const.user.getPassword());
            } else {
                editor.putBoolean("autoLogin", false);
            }
        }

        if(Const.neverShowMessageDialog!=null)
            editor.putString("neverShowMessageDialog", Const.neverShowMessageDialog);
        if(Const.neverShowVersionDialog !=null)
            editor.putString("neverShowVersionDialog", Const.neverShowVersionDialog);
        editor.apply();
    }

    public static void load() {
        Const.autoLoginUsername = sharedPreferences.getString("username", "");
        Const.autoLoginPassword = sharedPreferences.getString("password", "");
        Const.autoLogin = sharedPreferences.getBoolean("autoLogin", false);
        Const.agree = sharedPreferences.getBoolean("agree", false);
        Const.neverShowMessageDialog = sharedPreferences.getString("neverShowMessageDialog", null);
        Const.neverShowVersionDialog = sharedPreferences.getString("neverShowVersionDialog", null);
    }
}
