package com.example.giftme.Helpers;

import android.content.Context;
import android.os.StrictMode;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FCMSend {

    private static final String SERVER_KEY = "key=AAAAY_8GetM:APA91bHl4FSbF2U5j65sxWZZYv95Jwqm8nPVZgN1PQQrJluSbbcCInhicPDfmvKZ4BenXcsmewIe-I14qj5DcyqFDVzQOylXpq5GUttXzxEE2fvjbNLBVrZf0zd9jeKPLv8uVhtsQ5RP";
    private static final String BASE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String CONTENT_TYPE = "application/json";

    public static void pushNotification(Context context, String token, String title, String message) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);

        try {
            JSONObject json = new JSONObject();
            json.put("to", token);
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", message);
            json.put("notification", notification);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, json,
                    response -> System.out.println("Notification send successful!"),
                    error -> System.out.println("Notification send NOT successful!")) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", CONTENT_TYPE);
                    params.put("Authorization", SERVER_KEY);
                    return params;
                }
            };
            queue.add(jsonObjectRequest);
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
