package com.app.assist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

public class Utilities {

    public static  final String USER_PREF_NAME = "user_pref";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String LOGIN_STATUS = "status";

    public static void setCurrentUser(Context context,String name,String email){
        SharedPreferences.Editor mPref = context.getSharedPreferences(USER_PREF_NAME,Context.MODE_PRIVATE).edit();
        mPref.putString(NAME,name);
        mPref.putString(EMAIL,email);
        mPref.putBoolean(LOGIN_STATUS,true);
        mPref.apply();
    }

    public static boolean isLoggedIn(Context context){
        SharedPreferences mPref = context.getSharedPreferences(USER_PREF_NAME,Context.MODE_PRIVATE);
        return mPref.getBoolean(LOGIN_STATUS,false);
    }

    public static String getString(Context context,String fieldName){
        SharedPreferences mPref = context.getSharedPreferences(USER_PREF_NAME,Context.MODE_PRIVATE);
        return mPref.getString(fieldName,null);
    }


    public static void clearPreferences(Context context) {
        context.getSharedPreferences(USER_PREF_NAME,Context.MODE_PRIVATE).edit().clear().apply();
    }

    public static void showAlert(Context context, String title, String message, String positive, String negative){
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity)context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })

                .show();
    }

}
