package com.sikderithub.keyboard;

import android.util.Log;
import android.view.inputmethod.EditorInfo;

import com.android.inputmethod.latin.LatinIME;
import com.android.inputmethod.utils.GkEngine;
import com.android.inputmethod.utils.MyLatinIME;

public class SikderKeyBaord extends MyLatinIME {
    private static final String TAG = "SikderKeyBaord";
    @Override
    public void onStartInput(EditorInfo editorInfo, boolean restarting) {
        GkEngine.getGkFromLocal();
        Log.d(TAG, "onStartInput: ");
        super.onStartInput(editorInfo, restarting);
    }

    @Override
    public void onStartInputView(EditorInfo editorInfo, boolean restarting) {

        Log.d(TAG, "onStartInputView: ");
        super.onStartInputView(editorInfo, restarting);
    }
}
