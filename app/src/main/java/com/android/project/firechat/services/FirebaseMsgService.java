package com.android.project.firechat.services;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.android.project.firechat.AppNotificationManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

public class FirebaseMsgService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsg";

    // Keys to retrieve data
    private static final String TITLE = "title";
    private static final String BODY = "body";
    private static final String SENDER_UID = "senderUid";
    private static final String SENDER_NAME = "senderName";


    // Called both in background and foreground when receiving message from FCM/admin sdk
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData().size());

            if (isAppOnForeground(getApplicationContext())) {
                return;
            }
            String title = remoteMessage.getData().get(TITLE);
            String body = remoteMessage.getData().get(BODY);
            String senderUid = remoteMessage.getData().get(SENDER_UID);
            String senderName = remoteMessage.getData().get(SENDER_NAME);
            AppNotificationManager.getInstance(getApplicationContext())
                    .displayMessageNotification(title, senderName, body, senderUid);
        }
    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}