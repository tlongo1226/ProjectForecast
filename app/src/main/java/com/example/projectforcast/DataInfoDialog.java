package com.example.projectforcast;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

public class DataInfoDialog extends Dialog {
    public DataInfoDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_info_data);
        setTitle("Data Recording");
    }
}
