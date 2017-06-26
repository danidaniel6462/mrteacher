package com.darwindeveloper.mrteacher.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by Darwin Morocho on 13/3/2017.
 */

public class Extras {
    private Context context;

    public Extras(Context context) {
        this.context = context;
    }

    public AlertDialog.Builder createDialog(int icon, String title, String message, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(icon).setTitle(title)
                .setMessage(message).setCancelable(cancelable);

        return builder;

    }
}