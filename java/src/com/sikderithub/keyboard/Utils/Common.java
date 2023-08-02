package com.sikderithub.keyboard.Utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.sikderithub.keyboard.Models.Config;
import com.sikderithub.keyboard.MyApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Common {
    private static final String TAG = "Common";
    public static String LAST_AD_DATE_FORMAT = "yyyy-MM-dd";
    public static String INSTALL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String PREF_INSTALL_TIME_KEY = "PREF_INSTALL_TIME_KEY";


    public static String getInstallTime(){
        return PrefHelper.getPref(PREF_INSTALL_TIME_KEY, null);
    }

    public static void setInstallTimeIfNotExits(){
        if(getInstallTime()==null){
            SimpleDateFormat sdf = new SimpleDateFormat(Common.INSTALL_DATE_TIME_FORMAT,  Locale.getDefault());
            String currDateTime = sdf.format(new Date());
            PrefHelper.putPref(PREF_INSTALL_TIME_KEY, currDateTime);
        }
    }


    public static boolean isAdShownAllowed() {
        try{
            if(getInstallTime()==null){
                return false;
            }

            Config config = MyApp.getConfig();

            SimpleDateFormat sdf = new SimpleDateFormat(Common.INSTALL_DATE_TIME_FORMAT,  Locale.getDefault());

            Date currDate = sdf.parse(getCurrentDateTime());
            Date installLDate = sdf.parse(getInstallTime());


            long differenceInMillis = currDate.getTime() - installLDate.getTime();
            int daysDiff = (int) TimeUnit.DAYS.convert(differenceInMillis, TimeUnit.MILLISECONDS);
            Log.d(TAG, "isAdShownAllowed: "+daysDiff);
            if(daysDiff<=config.ad_hold_time-1){
                return false;
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public static String getCurrentDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat(Common.INSTALL_DATE_TIME_FORMAT,  Locale.getDefault());
        String currDateTime = sdf.format(new Date());

        return currDateTime;
    }


    public static boolean isFacebookLink(String linkUrl) {
        return linkUrl.contains("fb.com") || linkUrl.contains("facebook.com");
    }

    public static boolean isYouTubeLink(String linkUrl) {
        return linkUrl.contains("youtube.com") || linkUrl.contains("youtu.be");
    }

    public static boolean isPlayStoreLink(String linkUrl) {
        return linkUrl.contains("play.google.com");
    }

    public static boolean isValidUrl(String linkUrl) {
        // Regular expression pattern for validating URLs
        String urlPattern = "^(https?|ftp)://[\\w.-]+(\\.[a-zA-Z]{2,})+(/[\\w-./?%&=]*)?$";

        Pattern pattern = Pattern.compile(urlPattern);
        return pattern.matcher(linkUrl).matches();
    }

    private boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
