package com.yadercenteno.mensaje;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MiFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = "NOTICIAS";
    String titulo = "";
    String fecha = "";
    String lugar = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Estos son los datos (par / valor) recibidos del Firebase Cloud Messaging
        if (remoteMessage.getData().size() > 0) {
            titulo = remoteMessage.getData().get("Titulo");
            fecha = remoteMessage.getData().get("Fecha");
            lugar = remoteMessage.getData().get("Lugar");
        }

        mostrarNotificacion(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), titulo, fecha, lugar);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    private void mostrarNotificacion(String title, String body, String titulo, String fecha, String lugar) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Con esto aseguro mandar los datos obtenidos del Firebase Cloud Messaging
        intent.putExtra("Titulo", titulo);
        intent.putExtra("Fecha", fecha);
        intent.putExtra("Lugar", lugar);

        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, uniqueInt, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "Todas")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent);

        Random random = new Random();
        int num = random.nextInt(99999-1000)+1000;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("Todas", "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(num, notificationBuilder.build());
        }
    }
}
