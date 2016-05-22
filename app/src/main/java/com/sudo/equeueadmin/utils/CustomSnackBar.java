package com.sudo.equeueadmin.utils;


import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomSnackBar {
    public static void show(ViewGroup layout, String str) {
        Snackbar snackbar = Snackbar
                .make(layout, str, Snackbar.LENGTH_LONG);
        View view = (View) snackbar.getView();
        view.setBackgroundColor(Color.parseColor("#291545"));
        TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}
