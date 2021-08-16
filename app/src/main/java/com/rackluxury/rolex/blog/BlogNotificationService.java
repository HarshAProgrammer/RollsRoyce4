package com.rackluxury.rolex.blog;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.activities.ExpensiveCheckerActivity;

public class BlogNotificationService extends FirebaseMessagingService {


    private Bitmap bitmap;
    private BitmapDrawable drawable;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        drawable = (BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(),R.drawable.splashscreen);
        bitmap = drawable.getBitmap();

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        Uri image = remoteMessage.getNotification().getImageUrl();

        Intent resultIntent = new Intent(this, BlogActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setLargeIcon(bitmap)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);



    }

}
