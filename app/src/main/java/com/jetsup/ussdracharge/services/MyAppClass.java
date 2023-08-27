package com.jetsup.ussdracharge.services;

import static com.jetsup.ussdracharge.custom.ISPConstants.CHANNEL_NEWS;
import static com.jetsup.ussdracharge.custom.ISPConstants.CHANNEL_QUOTE_OFFLINE;
import static com.jetsup.ussdracharge.custom.ISPConstants.CHANNEL_QUOTE_ONLINE;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import java.util.ArrayList;
import java.util.List;

public class MyAppClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
        new NotificationHandler(this);
    }

    private void createNotificationChannels() {
        NotificationChannel offlineQuotesChannel = new NotificationChannel(
                CHANNEL_QUOTE_OFFLINE,
                "Quote of the Day",
                NotificationManager.IMPORTANCE_LOW
        );
        offlineQuotesChannel.setDescription("This notification will prompt quotes when you are offline");

        NotificationChannel onlineQuoteChannel = new NotificationChannel(
                CHANNEL_QUOTE_ONLINE,
                "Today's Random Quote",
                NotificationManager.IMPORTANCE_HIGH
        );
        onlineQuoteChannel.setDescription("This notification manages quotes received you get connected to the internet. Will show only once in a day.");

        NotificationChannel ispNewsChannel = new NotificationChannel(
                CHANNEL_NEWS,
                "ISP News",
                NotificationManager.IMPORTANCE_HIGH
        );
        ispNewsChannel.setDescription("You will receive promotional messages and offers of the day from your Service Provider");

        List<NotificationChannel> notificationChannels = new ArrayList<>();
        notificationChannels.add(offlineQuotesChannel);
        notificationChannels.add(onlineQuoteChannel);
        notificationChannels.add(ispNewsChannel);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannels(notificationChannels);
    }
}
