package com.example.giftme.Helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.example.giftme.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class PushNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String title = Objects.requireNonNull(message.getNotification()).getTitle();
        String body = message.getNotification().getBody();
        String CHANNEL_ID = "MESSAGE";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Message Notification", NotificationManager.IMPORTANCE_DEFAULT);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setAutoCancel(true);
            NotificationManagerCompat.from(this).notify(1, notification.build());
        }
        super.onMessageReceived(message);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext());
        dataBaseHelper.setDeviceMessagingToken(SessionManager.getUserEmail(getApplicationContext()));
        super.onNewToken(token);
    }
}
