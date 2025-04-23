package com.rahul.donate_a_food.Class;

import android.app.Application;
import android.content.Context;

import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;
import com.rahul.donate_a_food.R;

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
}
