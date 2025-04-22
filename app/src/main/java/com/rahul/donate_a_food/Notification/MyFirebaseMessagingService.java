package com.rahul.donate_a_food.Notification;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rahul.donate_a_food.R;

//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//
//    private static final String TAG = "FCM Service";
//
//    @Override
//    public void onNewToken(String token) {
//        super.onNewToken(token);
//        Log.d(TAG, "Refreshed token: " + token);
//
//        // Save the token to Firebase Database
//        saveTokenToDatabase(token);
//    }
//
//    private void saveTokenToDatabase(String token) {
//        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
//                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
//
//        if (userId != null) {
//            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
//            userRef.child("fcmToken").setValue(token);
//        }
//    }
//}

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(user.getUid())
                    .child("fcmToken")
                    .setValue(token);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "food_channel")
                .setSmallIcon(R.drawable.notifications)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("food_channel", "Food Alerts", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        manager.notify(101, builder.build());
    }
}


