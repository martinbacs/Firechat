package com.android.project.firechat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.project.firechat.activities.ChatActivity;
import com.android.project.firechat.shared.Constants;


public class AppNotificationManager {
    private Context mContext;
    private static AppNotificationManager mInstance;
    public static final String CHANNEL_ID = "FireChatMsg";
    public static final String CHANNEL_NAME = "FireChatMessaging";
    public static final String CHANNEL_DESCRIPTION = "Notifies chat messages";

    private AppNotificationManager(Context context) {
        mContext = context;
    }

    public static synchronized AppNotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppNotificationManager(context);

            // Only Android O uses notification channels
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = AppNotificationManager.CHANNEL_NAME;
                String description = AppNotificationManager.CHANNEL_DESCRIPTION;
                int importance = NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel channel = new NotificationChannel(AppNotificationManager.CHANNEL_ID, name, importance);
                channel.setDescription(description);
                channel.enableVibration(true);
                channel.enableLights(true);
                channel.setLightColor(0x00E5EE);
                channel.setVibrationPattern(new long[]{400, 400, 400});

                // Register the channel with the system
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
                Log.d("NotificationChannel", notificationManager.getNotificationChannels().toString());
            }
        }
        return mInstance;
    }

    // Builds and shows a notification tailored for received chat message
    public void displayMessageNotification(String title, String senderName, String body, String senderUid) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra(Constants.RECEIVER_UID_KEY, senderUid);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent
                , PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }
}
