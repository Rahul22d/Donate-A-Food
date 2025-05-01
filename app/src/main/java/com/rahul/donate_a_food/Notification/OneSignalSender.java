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

    public static void sendNotificationToTargetUser(Context context, String title, String message, String playerId) {
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
                        + "\"include_player_ids\": [\""+ playerId +"\"],"
                        + "\"headings\": {\"en\": \""+ title +"\"},"
                        + "\"contents\": {\"en\": \""+ message +"\"}"
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

    public static void sendNotificationToTargetUserForAcceptOrder(Context context, String title, String message, String playerId) {
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
                        + "\"include_player_ids\": [\""+ playerId +"\"],"
                        + "\"headings\": {\"en\": \""+ title +"\"},"
                        + "\"contents\": {\"en\": \""+ message +"\"}"
                        + "\"buttons\": ["
                        + "  {\"id\": \"accept\", \"text\": \"Accept\"},"
                        + "  {\"id\": \"reject\", \"text\": \"Reject\"}"
                        + "]"
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
