package com.example.administrator.h2bot.models;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.deliveryman.DeliveryManMainActivity;
import com.example.administrator.h2bot.waterstation.WaterStationMainActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;

import static android.support.constraint.Constraints.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService()
    {

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


//        if(remoteMessage.getData().size() > 0)
//        {
//            Log.d(TAG, "Message data payload: "+ remoteMessage.getData());
//            try{
//                JSONObject data = new JSONObject(remoteMessage.getData());
//                String jsonMessage = data.getString("extra_information");
//                Log.d(TAG, "onMessageReceived: \n"+
//                        "Extra Information: "+ jsonMessage);
//            }catch (JSONException e)
//            {
//                e.printStackTrace();
//            }
//        }
//        if(remoteMessage.getNotification() != null)
//        {
//            String title = remoteMessage.getNotification().getTitle();
//            String message = remoteMessage.getNotification().getBody();
//            String click_action = remoteMessage.getNotification().getClickAction();
//
//            Log.d(TAG, "Message Notification Title: "+ title);
//            Log.d(TAG, "Message Notification Body:"+ message);
//            Log.d(TAG, "Message Notification click_action"+ click_action);
//
//            sendNotification(title, message, click_action);
//        }
    }

    @Override
    public void onDeletedMessages() {

    }

    private void sendNotification(String title, String messageBody, String click_action)
    {
        Intent intent;
        if(click_action.equals("WATERSTATIONMAINACTIVITY"))
        {
            intent = new Intent(this, WaterStationMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        else if(click_action.equals("DELIVERYMANMAINACTIVITY"))
        {
            intent = new Intent(this, DeliveryManMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        else
        {
            intent = new Intent(this, WaterStationMainActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
