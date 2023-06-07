package com.android.inputmethod.utils;

import java.util.Locale;

public class Constant {
    public static String getLanguageName(Locale locale) {
        switch (locale.toString()){
            case "bn":
                return "অভ্র";
            case "bn_BD":
                return "বাংলা";
            case "en_US":
                return "English";
            default:
                return "";
        }

    }
}
