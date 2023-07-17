package com.sikderithub.keyboard.Service;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.sikderithub.keyboard.Models.NotificationData;
import com.sikderithub.keyboard.MyApp;
import com.sikderithub.keyboard.NotificationUtil;
import com.sikderithub.keyboard.local.Dao.QuestionDatabase;

import java.util.Map;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = "NotificationService";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if (message.getData().size() > 0) {

            NotificationData nData  = new NotificationData();

            Map<String, String> notificationData = message.getData();
            nData.title = notificationData.get("tittle");
            nData.description = notificationData.get("description");
            nData.imgUrl = notificationData.get("imgUrl");
            nData.notiClearAble = Integer.parseInt(notificationData.get("notiClearAble"));
            nData.action = Integer.parseInt(notificationData.get("action"));
            nData.notiType = Integer.parseInt(notificationData.get("notiType"));
            nData.actionUrl = notificationData.get("actionUrl");
            nData.actionActivity = notificationData.get("actionActivity");
            nData.inbox_notification = Integer.parseInt(notificationData.get("inbox_notification"));

            String hasValidityStr = notificationData.get("hasValidity");
            if (hasValidityStr != null) {
                nData.hasValidity = Integer.parseInt(hasValidityStr);
            }

            String expireTimeInSecStr = notificationData.get("expireTimeInSec");
            if (expireTimeInSecStr != null) {
                nData.expireTimeInSec = Long.parseLong(expireTimeInSecStr);
            }

            String issueTimeStr = notificationData.get("issueTime");
            if (issueTimeStr != null) {
                nData.issueTime = issueTimeStr;
            }





            if(nData.inbox_notification==1){
                QuestionDatabase.databaseWriteExecutor.execute(() -> {
                    QuestionDatabase.getDatabase(MyApp.getInstance())
                            .questionDAO()
                            .saveNotification(nData);
                });
            }else {
                NotificationUtil notificationUtil = new NotificationUtil(getApplicationContext());

                notificationUtil.displayNotification(nData);
            }




        }
    }

}
