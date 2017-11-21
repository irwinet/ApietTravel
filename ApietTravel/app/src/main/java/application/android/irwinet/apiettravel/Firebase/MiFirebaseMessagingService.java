package application.android.irwinet.apiettravel.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import application.android.irwinet.apiettravel.R;
import application.android.irwinet.apiettravel.SplashActivity;

/**
 * Created by Irwinet on 16/11/2017.
 */

public class MiFirebaseMessagingService extends FirebaseMessagingService{
    private static final String TAG = "NOTICIAS";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();
        Log.d(TAG,"Mensaje Recibifo: "+from);

        if(remoteMessage.getNotification()!=null)
        {
            Log.d(TAG,"Notificacion: "+remoteMessage.getNotification().getBody());

            showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        }

        if(remoteMessage.getData().size()>0)
        {
            Log.d(TAG,"Notificacion: "+remoteMessage.getData());
        }
    }

    private void showNotification(String title, String body) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri uriSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_event_black_24dp)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(uriSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());
    }
}
