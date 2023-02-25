package com.example.projectforcast;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class ConnectInfoDialog extends Dialog {
    public ConnectInfoDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_info_conn);
        setTitle("Connection Information");
        getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int dialogWidth = (int) (screenWidth * 0.6);
        getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
