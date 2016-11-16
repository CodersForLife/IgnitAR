package com.vizy.ignitar.utils;

import android.support.annotation.NonNull;

public class StringUtils {

    public static boolean isNullOrEmpty(@NonNull String value) {
        if (value == null || value.trim().equals("") || value.equals("null")) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
