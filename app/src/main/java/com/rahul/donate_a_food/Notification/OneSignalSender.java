package com.rahul.donate_a_food.Notification;

import static com.android.volley.BuildConfig.*;

import android.content.Context;
import android.util.Log;

import com.android.volley.BuildConfig;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rahul.donate_a_food.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.net.URL;


public class OneSignalSender {

    private static final String TAG = "OneSignalSender";
    private static final String URL = "https://asia-south1-project-b77f4.cloudfunctions.net/sendOneSignalNotification";

//    // Send notification to all users
//    public static void sendToAllUsers(Context context, String title, String message) {
//        sendRequest(context, buildJsonBodyForAll(title, message));
//    }
//
//    // Send to a specific OneSignal player ID
//    public static void sendToSpecificUser(Context context, String playerId, String title, String message) {
//        sendRequest(context, buildJsonBodyForSingle(title, message, playerId));
//    }
//
//    // Builds payload for ALL users
//    private static JSONObject buildJsonBodyForAll(String title, String message) {
//        JSONObject json = new JSONObject();
//        try {
//            json.put("title", title);
//            json.put("message", message);
//            json.put("target", "all");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return json;
//    }
//
//    // Builds payload for ONE user
//    private static JSONObject buildJsonBodyForSingle(String title, String message, String playerId) {
//        JSONObject json = new JSONObject();
//        try {
//            json.put("title", title);
//            json.put("message", message);
//            json.put("target", "single");
//            json.put("playerId", playerId);  // OneSignal player ID
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return json;
//    }
//
//    // Reusable network request
//    private static void sendRequest(Context context, JSONObject jsonBody) {
//        RequestQueue queue = Volley.newRequestQueue(context);
//
//        JsonObjectRequest request = new JsonObjectRequest(
//                Request.Method.POST,
//                URL,
//                jsonBody,
//                response -> Log.d(TAG, "Notification sent: " + response.toString()),
//                error -> Log.e(TAG, "Error sending notification: " + error.toString())
//        ) {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//        };
//
//        queue.add(request);
//    }

    public static void sendNotificationToAll(Context context) {
        new Thread(() -> {
            try {

                String oneSignalId = context.getString(R.string.one_signal_id);
                String restApiKey = context.getString(R.string.REST_API_KEY);

                Log.d("api key ","api key"+restApiKey + " " + oneSignalId);
                URL url = new URL("https://onesignal.com/api/v1/notifications");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);

                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                con.setRequestProperty("Authorization", restApiKey);
                con.setRequestMethod("POST");

                String requestBody = "{"
                        + "\"app_id\": \""+oneSignalId+"\","
                        + "\"included_segments\": [\"All\"],"
                        + "\"headings\": {\"en\": \"New Donation\"},"
                        + "\"contents\": {\"en\": \"New food donate request\"}"
                        + "}";

                byte[] sendBytes = requestBody.getBytes("UTF-8");
                con.getOutputStream().write(sendBytes);

                int httpResponse = con.getResponseCode();
                if (httpResponse >= HttpURLConnection.HTTP_OK && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                    InputStream inputStream = con.getInputStream();
                    Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                    String response = s.hasNext() ? s.next() : "";
                    Log.i("OneSignal", "Success: " + response);
                } else {
                    InputStream errorStream = con.getErrorStream();
                    Scanner s = new Scanner(errorStream).useDelimiter("\\A");
                    String errorResponse = s.hasNext() ? s.next() : "";
                    Log.e("OneSignal", "Error: " + errorResponse);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }).start();
    }

}
