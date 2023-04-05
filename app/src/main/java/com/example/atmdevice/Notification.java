package com.example.atmdevice;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
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


                if (snapshot.exists()) {

                    Map map = (Map) snapshot.getValue();

                    Integer temp = Integer.valueOf(map.get("temperature").toString());
                    Integer hum = Integer.valueOf(map.get("humidity").toString());
                    Integer gas = Integer.valueOf(map.get("gas").toString());


                    if(gas > 800 || temp > 32 || temp < 25 || hum > 60 || hum < 40){

                        if(mp == null) {
                            mp = MediaPlayer.create(Notification.this, R.raw.alarm);

                        }

                        if(!mp.isPlaying()){
                            mp.start();
                            mp.setLooping(true);
                            Toast.makeText(Notification.this, "Alarm.", Toast.LENGTH_SHORT).show();

                        }
                    }
                    else if(mp != null) {
                        mp.stop();
                        mp = null;
                        Toast.makeText(Notification.this, "normal.", Toast.LENGTH_SHORT).show();


                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

        return START_STICKY;


    }


}
