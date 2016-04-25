package com.sudo.equeue.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.sudo.equeue.R;
import com.sudo.equeue.WebSocketService;
import com.sudo.equeue.activities.StartActivity;

public class MyGcmListenerService extends GcmListenerService {

    public static final String ACTION_PUSH_INCOMING = "com.sudo.equeue.gcmlistener.action.incoming";
    public static final String EXTRA_QUEUE_ID = "com.sudo.equeue.gcmlistener.extra.qid";

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("title");
        int qid = Integer.parseInt(data.getString("qid"));
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);


        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(this, StartActivity.class), 0);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.people_pur)
                .setContentTitle("Ваша очередь!")
                .setContentText("Настала ваша очередь в '" + message + "'")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(7 /* ID of notification */, notificationBuilder.build());


        Intent pushMsg = new Intent(ACTION_PUSH_INCOMING);
        pushMsg.putExtra(EXTRA_QUEUE_ID, qid);
        LocalBroadcastManager.getInstance(this).sendBroadcast(pushMsg);

    }

    private void sendNotification(String message) {
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.location)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(7 /* ID of notification */, notificationBuilder.build());
    }
}