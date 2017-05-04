package com.estyle.teabaike.util;

import android.util.Log;

public class EstyleLog {

    public static void e(Object obj) {
        e("", obj);
    }

    public static void e(String className, Object obj) {
        Log.e("estyle", className + ": " + obj);
    }

}
