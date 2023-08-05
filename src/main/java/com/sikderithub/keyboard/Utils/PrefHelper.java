package com.sikderithub.keyboard.Utils;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.sikderithub.keyboard.MyApp;

public class PrefHelper {

    public static String PREF_LAST_AD_SHOW_KEY = "100100121";

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(MyApp.getApContext());
    }

    private static SharedPreferences.Editor getEditor() {
        SharedPreferences sp = getPreferences();
        if (sp != null)
            return sp.edit();

        return null;
    }

    public static int getPref(String key, int defValue) {
        SharedPreferences sp = getPreferences();
        if (sp == null)
            return defValue;
        return sp.getInt(key, defValue);
    }

    public static void putPref(String key, int val) {
        SharedPreferences.Editor spe = getEditor();
        if (spe == null)
            return;
        spe.putInt(key, val);
        spe.commit();
    }

    public static boolean getPref(String key, boolean defValue) {
        SharedPreferences sp = getPreferences();
        if (sp == null)
            return defValue;
        return sp.getBoolean(key, defValue);
    }

    public static void putPref(String key, boolean val) {
        SharedPreferences.Editor spe = getEditor();
        if (spe == null)
            return;
        spe.putBoolean(key, val);
        spe.commit();
    }

    public static long getPref(String key, long defValue) {
        SharedPreferences sp = getPreferences();
        if (sp == null)
            return defValue;
        return sp.getLong(key, defValue);
    }

    public static void putPref(String key, long val) {
        SharedPreferences.Editor spe = getEditor();
        if (spe == null)
            return;
        spe.putLong(key, val);
        spe.commit();
    }

    public static String getPref(String key, String defValue) {
        SharedPreferences sp = getPreferences();
        if (sp == null)
            return defValue;
        return sp.getString(key, defValue);
    }

    public static void putPref(String key, String val) {
        SharedPreferences.Editor spe = getEditor();
        if (spe == null)
            return;
        spe.putString(key, val);
        spe.commit();
    }

    public static void removePref(String key) {
        SharedPreferences.Editor spe = getEditor();
        if (spe == null)
            return;
        spe.remove(key);
        spe.commit();
    }

    public static void clear() {
        SharedPreferences.Editor spe = getEditor();
        if (spe == null)
            return;
        spe.clear();
        spe.commit();
    }
}