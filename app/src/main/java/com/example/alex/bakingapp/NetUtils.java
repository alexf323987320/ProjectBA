package com.example.alex.bakingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

class NetUtils {

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return (ni != null && ni.isConnected());
    }

}
