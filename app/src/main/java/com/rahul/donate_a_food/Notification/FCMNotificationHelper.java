//package com.rahul.donate_a_food.Notification;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.rahul.donate_a_food.R;
//
//public class FCMNotificationHelper {
//
//    private static final String TAG = "FCMNotificationHelper";
//
//    public static void notifyDonor(String donorId, Context context) {
//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(donorId).child("fcmToken");
//
//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    String fcmToken = snapshot.getValue(String.class);
//                    if (fcmToken != null) {
//                        // Trigger local notification
//                        showNotification(context, "New Order Request", "A receiver has placed an order.");
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e(TAG, "Error fetching donor FCM token: " + error.getMessage());
//            }
//        });
//    }
//
//    private static void showNotification(Context context, String title, String body) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "donation_channel")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setPriority(NotificationCompat.PRIORITY_HIGH);
//
//        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        manager.notify(1, builder.build());
//    }
//}
