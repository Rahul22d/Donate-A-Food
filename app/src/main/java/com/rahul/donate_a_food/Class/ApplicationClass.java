package com.rahul.donate_a_food.Class;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.rahul.donate_a_food.R;

import java.util.HashMap;
import java.util.Map;

public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable verbose logging for debugging (remove in production)
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
        // Initialize with your OneSignal App ID
        String api = getApplicationContext().getString(R.string.one_signal_id);
        OneSignal.initWithContext(this, api);
//        // Use this method to prompt for push notifications.
//        // We recommend removing this method after testing and instead use In-App Messages to prompt for notification permission.
//        OneSignal.getNotifications().requestPermission(false, Continue.none());

    }
    public static void savePlayerIdToFirebase(String playerId) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid())
                    .child("fcmToken")
                    .setValue(playerId);
        }
    }



}
