package com.mrehya.Pushe_Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mrehya.Lang;
import com.mrehya.Language;
import com.mrehya.MainActivity;
import com.mrehya.R;

import org.json.JSONException;
import org.json.JSONObject;

import co.ronash.pushe.PusheListenerService;

//import co.ronash.pushe.PusheListenerService;

/**
 * Created by ashke98 on 6/6/2018.
 */



public class MyPushListener extends PusheListenerService {
    //@Override
    public void onMessageReceived(JSONObject customContent, JSONObject pushMessage) {
        if (customContent == null || customContent.length() == 0)
            return; //json is empty
        Log.w("Pushe", "Custom json Message: " + customContent.toString()); //print json to logCat
        //Do something with json
        try {
            String s1 = customContent.getString("title");
            String s2 = customContent.getString("content");
            Log.w("Pushe", "Json Message\n title: " + s1 + "\n content: " + s2);

//
            Intent i =  new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingintent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            //NOTIFICATION PART
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle(s1)
                    .setContentText(s2)
                    .setSmallIcon(R.drawable.ic_notif)
                    .setContentIntent(pendingintent);

            NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());

        } catch (JSONException e) {
            android.util.Log.e("TAG", "Exception in parsing json", e);
        }
    }
}

