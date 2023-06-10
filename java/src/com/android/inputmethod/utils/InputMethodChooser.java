package com.android.inputmethod.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;

import com.sikderithub.keyboard.R;

public class InputMethodChooser {

    public  Dialog dialog;


    public InputMethodChooser(Context ctx){
        Log.d("TAG", "InputMethodChooser: ");
        dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_radio_group);

        dialog.show();
    }







}
