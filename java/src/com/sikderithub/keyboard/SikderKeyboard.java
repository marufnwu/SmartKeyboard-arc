package com.sikderithub.keyboard;

import android.content.Intent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import com.android.inputmethod.latin.PhoneticBangla;
import com.android.inputmethod.utils.GkEngine;
import com.android.inputmethod.utils.MyLatinIME;
import com.google.gson.Gson;
import com.sikderithub.keyboard.Activity.UpdateActivity;
import com.sikderithub.keyboard.Utils.CustomThemeHelper;

import java.util.Map;
import java.util.function.BiConsumer;

public class SikderKeyboard extends MyLatinIME {
    private static final String TAG = "SikderKeyBaord";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");


        //phoneticKeyMap();

    }

    private void phoneticKeyMap() {
        PhoneticBangla phoneticBangla = new PhoneticBangla();

        printKeyMap(phoneticBangla.bbr);
        printKeyMap(phoneticBangla.jbr);
        printKeyMap(phoneticBangla.kar);
        printKeyMap(phoneticBangla.srb);
        printKeyMap(phoneticBangla.other);
    }

    private void printKeyMap(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(key + "=> " + value);
        }
    }

    @Override
    public void onStartInput(EditorInfo editorInfo, boolean restarting) {
        Log.d(TAG, "onStartInput: ");
        super.onStartInput(editorInfo, restarting);
    }

    @Override
    public void onStartInputView(EditorInfo editorInfo, boolean restarting) {


        MyApp.getConfigFromServer();
        MyApp.getLatestQuestion();
        Log.d(TAG, "onStartInputView: "+new Gson().toJson(MyApp.getUpdateInfo()));

        MyApp.initLocalConfig();

        if(MyApp.getUpdateInfo().version_code>BuildConfig.VERSION_CODE && MyApp.getUpdateInfo().status==1){
            //updateAvailable
            if(MyApp.getUpdateInfo().update_type==1){
                startActivity(new Intent(this, UpdateActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }
        super.onStartInputView(editorInfo, restarting);


    }

    @Override
    public void onWindowHidden() {
        Log.d(TAG, "onWindowHidden: ");
        super.onWindowHidden();
    }

    @Override
    public void onWindowShown() {
        Log.d(TAG, "onWindowShown: ");
        super.onWindowShown();
    }

}
