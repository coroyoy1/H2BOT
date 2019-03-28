package com.example.administrator.h2bot;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notification extends Application {
    public static final String CHANNEL_1_ID = "notificationforcomplete";
    public static final String CHANNEL_2_ID = "notificationforcanceled";
    public static final String CHANNEL_3_ID = "notificationforpending";
    @Override
    public void onCreate() {
        super.onCreate();

    }
    private void createNotificationChannels()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationforcomplete = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Notification for Completed Orders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationforcomplete.setDescription("Transaction is Complete");

            NotificationChannel notificationforcanceled = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Notification for Canceled Orders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationforcanceled.setDescription("Transaction is Canceled");

            NotificationChannel notificationforpending = new NotificationChannel(
                    CHANNEL_3_ID,
                    "Notification for Pending Orders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationforpending.setDescription("Transaction is Pending");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationforcomplete);
            manager.createNotificationChannel(notificationforcanceled);
            manager.createNotificationChannel(notificationforpending);
        }
    }
}
