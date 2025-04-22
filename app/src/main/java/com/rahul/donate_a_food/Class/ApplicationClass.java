package com.rahul.donate_a_food.Class;

import android.app.Application;

import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;

public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable verbose logging for debugging (remove in production)
        OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
        // Initialize with your OneSignal App ID
        OneSignal.initWithContext(this, "b53146a6-b548-4784-8cfe-1e0103fd5dcb");
//        // Use this method to prompt for push notifications.
//        // We recommend removing this method after testing and instead use In-App Messages to prompt for notification permission.
//        OneSignal.getNotifications().requestPermission(false, Continue.none());



    }
}
