package com.example.wala;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MySMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) return;

            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus == null) return;

            SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }

            if (messages.length > 0) {
                String messageBody = messages[0].getMessageBody();
                String phoneNumber = messages[0].getDisplayOriginatingAddress();

                // Cas 1 : demande de position
                if (messageBody.contains("FindMyFriends: Envoyer moi votre position")) {
                    Intent serviceIntent = new Intent(context, MyGPSLocationService.class);
                    serviceIntent.putExtra("sender", phoneNumber);
                    context.startService(serviceIntent);
                }

                // Cas 2 : réception d'une position
                if (messageBody.startsWith("FindMyFriends: ma position est")) {
                    String[] t = messageBody.split("#");
                    if (t.length >= 3) {
                        double longitude = Double.parseDouble(t[1]);
                        double latitude = Double.parseDouble(t[2]);

                        // Notification
                        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context, "FindMyFriends_ChannelID")
                                .setContentTitle("Position reçue")
                                .setContentText("Appuyez pour voir sur la carte")
                                .setSmallIcon(android.R.drawable.ic_dialog_map)
                                .setAutoCancel(true);

                        Intent mapIntent = new Intent(context, MapssActivity.class);
                        mapIntent.putExtra("longitude", longitude);
                        mapIntent.putExtra("latitude", latitude);

                        PendingIntent pi = PendingIntent.getActivity(
                                context,
                                0,
                                mapIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        );

                        notifBuilder.setContentIntent(pi);

                        // Créer le canal si nécessaire
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel canal = new NotificationChannel(
                                    "FindMyFriends_ChannelID",
                                    "Canal FindMyFriends",
                                    NotificationManager.IMPORTANCE_DEFAULT
                            );
                            NotificationManager nm = context.getSystemService(NotificationManager.class);
                            if (nm != null) nm.createNotificationChannel(canal);
                        }

                        NotificationManagerCompat.from(context).notify(1, notifBuilder.build());
                    }
                }
            }
        }
    }
}
