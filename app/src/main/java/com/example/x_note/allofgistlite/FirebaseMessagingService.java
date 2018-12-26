package com.example.x_note.allofgistlite;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FBMsgService";

    //[START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){

        if(remoteMessage.getData().size()>0) {
            String bodyStrFromServer = remoteMessage.getData().get("body");

            //꺼진 화면 키기
            /*PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if(powerManager!=null) {
                PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "TAG");
                wakeLock.acquire(3000);
            }*/

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            //오레오 버전에서는 아래와 같이 해주어야 알람이 뜸
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("default", "기본 채널",
                        NotificationManager.IMPORTANCE_HIGH);

                mNotificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this,"default")
                            .setSmallIcon(R.drawable.alarm_white)
                            .setContentTitle("ALL OF GIST")
                            .setContentText(bodyStrFromServer)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(this, MainActivity.class);


            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);

            long[] vibrate = {0, 100, 200, 300};
            mBuilder.setVibrate(vibrate);
            mBuilder.setAutoCancel(true);

            // mId allows you to update the notification later on.
            if(mNotificationManager!=null)
                mNotificationManager.notify(0, mBuilder.build());
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.e("NEW_TOKEN",token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token){
        SharedPreferences prefs = getSharedPreferences("TOKEN_PREF", MODE_PRIVATE);
        String tokenPref = prefs.getString("token","");
        if(TextUtils.isEmpty(tokenPref)) {
            SharedPreferences.Editor editor = getSharedPreferences("TOKEN_PREF", MODE_PRIVATE).edit();
            editor.putString("token",token);
            editor.apply();
        }
        else if(!token.equals(tokenPref)){
            SharedPreferences.Editor editor = getSharedPreferences("TOKEN_PREF", MODE_PRIVATE).edit();
            editor.putString("token",token);
            editor.apply();
        }
        else{}
    }


}
