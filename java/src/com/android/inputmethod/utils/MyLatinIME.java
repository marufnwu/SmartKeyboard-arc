package com.android.inputmethod.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.inputmethod.keyboard.Key;
import com.android.inputmethod.keyboard.MoreKeysPanel;
import com.android.inputmethod.keyboard.PointerTracker;
import com.android.inputmethod.keyboard.internal.DrawingProxy;
import com.android.inputmethod.latin.LatinIME;
import com.sikderithub.keyboard.R;
import com.android.inputmethod.latin.common.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MyLatinIME extends LatinIME implements DrawingProxy {
    static final String TAG = MyLatinIME.class.getSimpleName();
    List<InputMethodSubtype> listOfInputMethods = new ArrayList<>();



    @Override
    public void onCreate() {
        super.onCreate();

        languageSwitcher.loadLocales(PreferenceManager.getDefaultSharedPreferences(this));
        int subTypes = mRichImm.getInputMethodInfoOfThisIme().getSubtypeCount();
        for (int i=0; i<subTypes; i++){
            InputMethodSubtype subType = mRichImm.getInputMethodInfoOfThisIme().getSubtypeAt(i);
            listOfInputMethods.add(subType);
            Log.d(TAG, "onCreate: "+subType.getLocale());
        }





    }

    @Override
    public boolean onCustomRequest(final int requestCode) {
        if (isShowingOptionDialog()) return false;
        switch (requestCode) {
            case Constants.CUSTOM_CODE_SHOW_INPUT_METHOD_PICKER:
                showDialog();
                if (mRichImm.hasMultipleEnabledIMEsOrSubtypes(true /* include aux subtypes */)) {
                    mRichImm.getInputMethodManager().showInputMethodPicker();

                    return true;
                }
                return true;
            case Constants.KEYCODE_NEXT_LANGUAGE:
            case Constants.KEYCODE_PREV_LANGUAGE:
                toogleLanguage(requestCode);
                return false;
        }
        return false;
    }

    private void toogleLanguage(int requestCode) {
        Log.d(TAG, "toogleLanguage: "+requestCode);
        if(requestCode==Constants.KEYCODE_NEXT_LANGUAGE){
            languageSwitcher.next();
        }else{
            languageSwitcher.prev();
        }
        languageSwitcher.persist();
        Log.d(TAG, "toogleLanguage: "+languageSwitcher.getInputLocale());

        int index = 0;

        for (InputMethodSubtype type: listOfInputMethods) {
            if (Objects.equals(type.getLocale(), languageSwitcher.getInputLocale().toString())){
                index = listOfInputMethods.indexOf(type);
                break;
            }
        }

        mHandler.postSwitchLanguage(listOfInputMethods.get(index));
    }


    protected void showDialog() {
        Log.d(TAG, "showDialog: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make your selection");

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_radio_group, null);

        RadioGroup grp = customLayout.findViewById(R.id.dialog_radio_group);

        int subTypes = mRichImm.getInputMethodInfoOfThisIme().getSubtypeCount();
        List<InputMethodSubtype> listOfInputMethods = new ArrayList<>();

        for (int i=0; i<subTypes; i++){
            InputMethodSubtype subType = mRichImm.getInputMethodInfoOfThisIme().getSubtypeAt(i);
            listOfInputMethods.add(subType);
            RadioButton btn = new RadioButton(this);
            String name = "";

            switch (subType.getLocale().toLowerCase()){
                case "bn":
                    name = "Bangla Phonetic";
                    break;
                case "bn_bd":
                    name = "Bangla Akkhor";
                    break;
                default:
                    name = "English";
            }

            btn.setText(name);


            grp.addView(btn);

            if(Objects.equals(subType.getLocale(), mRichImm.getCurrentSubtype().getRawSubtype().getLocale())){
                grp.check(btn.getId());
            }else {
                Log.d(TAG, "subType: not matched");
            }
        }

        builder.setView(customLayout);


        AlertDialog alert = builder.create();
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int index = group.indexOfChild(group.findViewById(checkedId));
                //switchLanguage(listOfInputMethods.get(index));
                mHandler.postSwitchLanguage(listOfInputMethods.get(index));

                alert.dismiss();
            }
        });



        Window window = alert.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.token = mInputView.getWindowToken();
        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alert.show();
    }

    @Override
    public void onKeyPressed(@NonNull Key key, boolean withPreview) {
        Log.d(TAG, "onKeyPressed: "+key);
    }

    @Override
    public void onKeyReleased(@NonNull Key key, boolean withAnimation) {

    }

    @Nullable
    @Override
    public MoreKeysPanel showMoreKeysKeyboard(@NonNull Key key, @NonNull PointerTracker tracker) {
        return null;
    }

    @Override
    public void startWhileTypingAnimation(int fadeInOrOut) {

    }

    @Override
    public void showSlidingKeyInputPreview(@Nullable PointerTracker tracker) {

    }

    @Override
    public void showGestureTrail(@NonNull PointerTracker tracker, boolean showsFloatingPreviewText) {

    }

    @Override
    public void dismissGestureFloatingPreviewTextWithoutDelay() {

    }


}
