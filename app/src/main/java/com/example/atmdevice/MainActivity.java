package com.example.atmdevice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Provider;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar1,progressBar2, progressBar3;
    TextView textView1, textView2, textView3, normal;
    ImageView im1, im2;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switch1;
    int gas_status, temp_status, hum_status;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference mRef;

    public static final String SHARED_PREFS = "sharedPrefs";



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = findViewById(R.id.text_view_progress1);
        textView2 = findViewById(R.id.text_view_progress2);
        textView3 = findViewById(R.id.text_view_progress3);
        switch1 = findViewById(R.id.sw);


        im1 = findViewById(R.id.mute);
        im2 = findViewById(R.id.unmute);
        im1.setVisibility(View.GONE);
        im2.setVisibility(View.VISIBLE);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean sw = sharedPreferences.getBoolean("bl",false);


        if (sw.equals(true)){

            switch1.setChecked(true);
            im1.setVisibility(View.VISIBLE);
            im2.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Alarm Mode Deactivated", Toast.LENGTH_SHORT).show();

        }else{

            editor.clear();
            editor.apply();
            im1.setVisibility(View.GONE);
            im2.setVisibility(View.VISIBLE);
            startService(new Intent(MainActivity.this,Notification.class));
            Toast.makeText(MainActivity.this, "Alarm Mode Activated", Toast.LENGTH_SHORT).show();

        }

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    editor.putBoolean("bl", switch1.isChecked());
                    editor.apply();
                    im1.setVisibility(View.VISIBLE);
                    im2.setVisibility(View.GONE);
                    stopService(new Intent(MainActivity.this,Notification.class));
                    Toast.makeText(MainActivity.this, "Alarm Mode Deactivated", Toast.LENGTH_SHORT).show();
                }
                  else {

                    editor.clear();
                    editor.apply();
                    im1.setVisibility(View.GONE);
                    im2.setVisibility(View.VISIBLE);
                    startService(new Intent(MainActivity.this,Notification.class));
                    Toast.makeText(MainActivity.this, "Alarm Mode Activated", Toast.LENGTH_SHORT).show();


                }
            }
        });

        normal = findViewById(R.id.normal);

        normal.setVisibility(View.GONE);

        progressBar1 = (ProgressBar) findViewById(R.id.progress_bar1);
        progressBar2 = (ProgressBar) findViewById(R.id.progress_bar2);
        progressBar3 = (ProgressBar) findViewById(R.id.progress_bar3);


        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();

        getdata();


    }

    private void getdata() {

        mRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    Map map = (Map)snapshot.getValue();

                    Integer temp = Integer.valueOf(map.get("temperature").toString());
                    Integer hum = Integer.valueOf(map.get("humidity").toString());
                    Integer gas = Integer.valueOf(map.get("gas").toString());
                    temp_status = temp;
                    progressBar1.setProgress((temp));
                    textView1.setText(Integer.toString(temp)+" °C");

                    hum_status = hum;
                    progressBar2.setProgress((hum));
                    textView2.setText(Integer.toString(hum)+ " %");

                    float x = (float) Math.round(gas);
                    float val = ((x/1024)*100);
                    Integer val_b = (int)Math.round(val);
                    gas_status = val_b;
                    progressBar3.setProgress(val_b);
                    textView3.setText(String.valueOf(val_b));

                    normal.setVisibility(View.VISIBLE);

                    if(gas > 800 || temp > 32 || temp < 25 || hum > 60 || hum < 40){

                        normal.setText("Danger! Room's environment is not normal.");

                    }else {
                        normal.setText("Room's environment is normal.");

                    }
                    normal.setTextColor(Color.RED);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

}