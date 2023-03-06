package com.jetsup.ussdracharge.services;


import static com.jetsup.ussdracharge.custom.ISPConstants.CHANNEL_NEWS;
import static com.jetsup.ussdracharge.custom.ISPConstants.CHANNEL_QUOTE_OFFLINE;
import static com.jetsup.ussdracharge.custom.ISPConstants.CHANNEL_QUOTE_ONLINE;
import static com.jetsup.ussdracharge.custom.ISPConstants.NOTIFICATION_CHANNEL_ISP_PROMO_NEWS_ID;
import static com.jetsup.ussdracharge.custom.ISPConstants.NOTIFICATION_CHANNEL_OFFLINE_QUOTES_ID;
import static com.jetsup.ussdracharge.custom.ISPConstants.NOTIFICATION_CHANNEL_ONLINE_QUOTES_ID;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.jetsup.ussdracharge.R;

public class NotificationHandler {
    //    final String TAG = "MyTag";
    Context context;
    NotificationManagerCompat notificationManagerCompat;

    public NotificationHandler(Context context) {
        this.context = context;
        notificationManagerCompat = NotificationManagerCompat.from(context);
        sendDailyQuoteNotification();
        sendIspNewsUpdateNotification();
    }

    private void sendDailyQuoteNotification() {
        Notification offlineNotification = new NotificationCompat.Builder(context, CHANNEL_QUOTE_OFFLINE)
                .setSmallIcon(R.drawable.baseline_format_quote_24)
                .setContentTitle("Favourite Quote")
                .setContentText("This was said by Steve Jobs")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        Notification onlineNotification = new NotificationCompat.Builder(context, CHANNEL_QUOTE_ONLINE)
                .setSmallIcon(R.drawable.moon)
                .setContentTitle("Today's Quote in the Internet")
                .setContentText("This is what online API generated")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(NOTIFICATION_CHANNEL_OFFLINE_QUOTES_ID, offlineNotification);
        notificationManagerCompat.notify(NOTIFICATION_CHANNEL_ONLINE_QUOTES_ID, onlineNotification);
    }

    private void sendIspNewsUpdateNotification() {
        Notification ispPromoNewsNotification = new NotificationCompat.Builder(context, CHANNEL_NEWS)
                .setSmallIcon(R.drawable.baseline_tips_and_updates_24)
                .setContentTitle("News from ISP")
                .setContentText("Safaricom has new offers today")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_PROMO)
                .build();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(NOTIFICATION_CHANNEL_ISP_PROMO_NEWS_ID, ispPromoNewsNotification);

    }

}
