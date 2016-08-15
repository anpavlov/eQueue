package com.sudo.equeueadmin.utils;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class AlertDialogHelper {
    public static void show(Context context, String str) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(str);
        alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        alertDialogBuilder.create().show();
    }
}
