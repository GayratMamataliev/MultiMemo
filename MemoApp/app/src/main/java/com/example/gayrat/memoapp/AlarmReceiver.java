package com.example.gayrat.memoapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;

public class AlarmReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String image = intent.getStringExtra("image");
        String text = intent.getStringExtra("text");
        Bitmap imagebit = memo.decodeBase64(image);
        Notification notification = new Notification.Builder(context)
                .setContentTitle("Memo")
                .setContentText(text)
                .setLargeIcon(imagebit)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, WritingMemoActivity.class), 0))
                .build();

        notificationManager.notify(0, notification);
    }
}