package com.vathanakmao.solarnoon.util;

import android.app.AlertDialog;
import android.content.Context;

public class DialogFactory {

    public static void showNewInfoDialog(String message, Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Info")
                .setMessage(message)
                .setNeutralButton("OK", (dialog, which) -> dialog.dismiss())
                .create().show();
    }
}
