package com.example.atmdevice;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
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

    private String TAG = "BroadcastService";
    public static final String COUNTDOWN_BR = "com.example.backgoundtimercount";
    Intent intent = new Intent(COUNTDOWN_BR);
    CountDownTimer countDownTimer = null;
    SharedPreferences sharedPreferences;
    MediaPlayer mp, np;
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

                    Float spo2 = Float.valueOf(map.get("spo2").toString());

                    Float temp = Float.valueOf(map.get("temperature").toString());
                    Float hum = Float.valueOf(map.get("humidity").toString());
                    Integer gas = Integer.valueOf(map.get("gas").toString());

                    Float tmax = Float.valueOf(map.get("tempmax").toString());
                    Float hmax = Float.valueOf(map.get("hummax").toString());
                    Integer gmax = Integer.valueOf(map.get("gasmax").toString());

                    Float tmin = Float.valueOf(map.get("tempmin").toString());
                    Float hmin = Float.valueOf(map.get("hummin").toString());
                    Integer gmin = Integer.valueOf(map.get("gasmin").toString());
                    Integer buttonalarm = Integer.valueOf(map.get("buttonalarm").toString());

                    if(buttonalarm == 1){
                        if(np == null) {
                            np = MediaPlayer.create(Notification.this, R.raw.emergency);
                            np.start();
                            np.setLooping(true);


                        }

                    }else{
                        if(np != null) {
                            if(np.isPlaying()) {
                                np.stop();
                                np= null;
                            }
                        }

                    }

                    if(gas > gmax || gas < gmin|| temp > tmax || temp < tmin || hum > hmax || hum < hmin || (spo2<94 && spo2>0) ){

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

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //create an intent that you want to start again.
        Intent intent = new Intent(getApplicationContext(), Notification.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
        super.onTaskRemoved(rootIntent);
    }

}