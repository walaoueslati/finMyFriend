package com.example.wala;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MyGPSLocationService extends Service {

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String number = intent.getStringExtra("sender");

        // Vérifier permission location
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("MyGPSLocationService", "Permission ACCESS_FINE_LOCATION non accordée");
            stopSelf();
            return START_NOT_STICKY;
        }

        FusedLocationProviderClient mClient =
                LocationServices.getFusedLocationProviderClient(this);

        mClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

                    // Vérifier permission SMS avant d'envoyer
                    if (ContextCompat.checkSelfPermission(MyGPSLocationService.this,
                            android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

                        SmsManager manager = SmsManager.getDefault();
                        manager.sendTextMessage(number, null,
                                "FindMyFriends: ma position est#" + longitude + "#" + latitude,
                                null, null);
                    } else {
                        Log.e("MyGPSLocationService", "Permission SEND_SMS non accordée");
                    }
                }
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
