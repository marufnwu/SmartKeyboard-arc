package com.sikderithub.keyboard.Models;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.android.inputmethod.latin.settings.SettingsActivity;
import com.sikderithub.keyboard.CommonMethod;

@Entity(tableName = "notification_table")
public class NotificationData {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int action;
    public int notiClearAble;
    public int notiType;
    public String title;
    public String description;
    public String actionActivity;
    public String actionUrl;
    public String imgUrl;
    public int hasValidity;
    public long expireTimeInSec;
    public String issueTime;
    public int inbox_notification;
    public boolean seen = false;


    public NotificationData() {
    }


    public void onNotificationAction(Context context){
        Intent resultIntent = null;

        if (action == 1) {
            //open url
            Log.d("intentSelect", "Url");
            Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
            notificationIntent.setData(Uri.parse(actionUrl));

            String url = actionUrl;
            CommonMethod.INSTANCE.openLink(context, url);
            return;
        } else if (action == 2) {
            //open activity
            Log.d("intentSelect", "Activity");
            String activity = actionActivity;

            resultIntent = new Intent(context, SettingsActivity.class);

        } else {
            Log.d("intentSelect", "Splash Activity");
            //resultIntent = new Intent(context, SettingsActivity.class);

        }
        
        if(resultIntent!=null){
            context.startActivity(resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }
}