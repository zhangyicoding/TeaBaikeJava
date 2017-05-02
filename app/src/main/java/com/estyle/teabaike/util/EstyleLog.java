package com.estyle.teabaike.util;

import android.util.Log;

import static android.R.id.message;

public class EstyleLog {

    public static void e(String className, Object obj) {
        Log.e("estyle", className + ": " + obj);
    }

}
