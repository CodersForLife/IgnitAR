package com.vizy.ignitar.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;

public class Utility {

    public static void showProgressDialog(@NonNull Activity activity, @NonNull ProgressDialog progressDialog) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Loading");
            progressDialog.setIndeterminate(true);
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }
    }

    public static void hideProgressDialog(@NonNull ProgressDialog progressDialog) {
        if ((progressDialog != null) && (progressDialog.isShowing())) {
            progressDialog.hide();
        }
    }
}
