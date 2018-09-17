package com.github.daanikus.paintracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.app.Notification.VISIBILITY_PUBLIC;

public class AlertReceiver extends BroadcastReceiver {
    private NotificationManager mNotificationManager;
    private static final String TITLE = "Welcome";
    private static final String CONTENT = "Create an entry, click the + button.";
    private static long mostRecent = 0;
    private static final long WAIT = 0;
    private static int count = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(context, MyNotificationChannel.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(TITLE)
                .setContentText(CONTENT)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setVisibility(VISIBILITY_PUBLIC)
                .setColor(Color.BLUE)
                .build();
        if ((mostRecent+WAIT) < System.currentTimeMillis()) {
            mNotificationManager.notify(1, notification);
            count++;
            Log.i("Condition", ""+mostRecent+" "+count);
        }
    }

    public AlertReceiver() {

    }

    public static void setMostRecent(long mostRecent) {
        AlertReceiver.mostRecent = mostRecent;
    }
}