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
    private static String title = "";
    private static String content = "";
    private static long mostRecent = 0;
    private static long wait = 0;
    private static int count = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        // If notifications disabled in settings, don't do anything
        if (!MainActivity.notificationsOn) return;

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if ((mostRecent+wait) < System.currentTimeMillis()) {
            if(Stats.getTotalEntries() == 0) {
                sendWelcome();
            } else {
                sendReminder();
            }

            Notification notification = new NotificationCompat.Builder(context, MyNotificationChannel.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setVisibility(VISIBILITY_PUBLIC)
                .setColor(Color.BLUE)
                .build();

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

    public void sendWelcome(){
        this.title = "Welcome";
        this.content = "Create an entry, click the + button.";
    }

    public void sendReminder(){
        this.title = "Reminder";
        this.content = "It's been a while since your last entry.";
    }
}