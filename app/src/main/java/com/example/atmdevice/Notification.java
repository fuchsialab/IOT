package com.example.atmdevice;


import static android.app.AlarmManager.ELAPSED_REALTIME;
import static android.os.SystemClock.elapsedRealtime;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Notification extends Service {

    MediaPlayer mp;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRef= firebaseDatabase.getReference();
    public static final String SHARED_PREFS = "sharedPrefs";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Boolean sw = sharedPreferences.getBoolean("bl",false);



                if (snapshot.exists()) {

                    Map map = (Map) snapshot.getValue();
                    Integer temp = Integer.valueOf(map.get("temperature").toString());
                    Integer hum = Integer.valueOf(map.get("humidity").toString());
                    Integer gas = Integer.valueOf(map.get("gas").toString());


                    if(gas > 800 || temp > 32 || temp < 25 || hum > 60 || hum < 40){


                        if (sw.equals(false)){

                            if(mp == null) {
                                mp = MediaPlayer.create(Notification.this, R.raw.alarm);

                            }

                            if(!mp.isPlaying()){
                                mp.start();
                                mp.setLooping(true);

                            }


                        }else{


                        }


                    }
                    else if(mp != null) {
                        mp.stop();
                        mp = null;

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

        return START_STICKY;


    }

    @Override
    public void onDestroy() {

        if(mp != null) {
            mp.stop();
            mp = null;
        }

    }

}
