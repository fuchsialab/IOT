package com.example.atmdevice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar1, progressBar2, progressBar3;
    TextView textView1, textView2, textView3, normal, spo2percentage, spo2message, bpmCount;
    ImageView im1, im2;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switch1;
    Button btn, spo2btn;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mRef, alart, spo2ref, bmmRef;

    public static final String SHARED_PREFS = "sharedPrefs";
    int a = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = findViewById(R.id.text_view_progress1);
        textView2 = findViewById(R.id.text_view_progress2);
        textView3 = findViewById(R.id.text_view_progress3);
        switch1 = findViewById(R.id.sw);

        spo2percentage = findViewById(R.id.spo2percentage);
        bpmCount = findViewById(R.id.bpm);
        spo2message = findViewById(R.id.spo2message);
        spo2btn = findViewById(R.id.spo2btn);



        btn = findViewById(R.id.calib);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity (new Intent (MainActivity.this, Calibration.class));
            }
        });

        im1 = findViewById(R.id.mute);
        im2 = findViewById(R.id.unmute);
        im1.setVisibility(View.GONE);
        im2.setVisibility(View.VISIBLE);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean sw = sharedPreferences.getBoolean("bl", false);


        if (sw.equals(true)) {

            switch1.setChecked(true);
            im1.setVisibility(View.VISIBLE);
            im2.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Alarm Mode Deactivated", Toast.LENGTH_SHORT).show();

        } else {

            editor.clear();
            editor.apply();
            im1.setVisibility(View.GONE);
            im2.setVisibility(View.VISIBLE);
            startService(new Intent(MainActivity.this, Notification.class));
            Toast.makeText(MainActivity.this, "Alarm Mode Activated", Toast.LENGTH_SHORT).show();

        }

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    editor.putBoolean("bl", switch1.isChecked());
                    editor.apply();
                    im1.setVisibility(View.VISIBLE);
                    im2.setVisibility(View.GONE);
                    stopService(new Intent(MainActivity.this, Notification.class));
                    Toast.makeText(MainActivity.this, "Alarm Mode Deactivated", Toast.LENGTH_SHORT).show();
                } else {

                    editor.clear();
                    editor.apply();
                    im1.setVisibility(View.GONE);
                    im2.setVisibility(View.VISIBLE);
                    startService(new Intent(MainActivity.this, Notification.class));
                    Toast.makeText(MainActivity.this, "Alarm Mode Activated", Toast.LENGTH_SHORT).show();


                }
            }
        });

        normal = findViewById(R.id.normal);

        normal.setVisibility(View.GONE);

        progressBar1 = findViewById(R.id.progress_bar1);
        progressBar2 = findViewById(R.id.progress_bar2);
        progressBar3 = findViewById(R.id.progress_bar3);


        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();


        getdata();

        mRef = FirebaseDatabase.getInstance().getReference();
        alart = mRef.child("buttonalarm");

        spo2ref = mRef.child("spo2");
        bmmRef = mRef.child("bpm");
        spo2btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                spo2ref.setValue(0);
                bmmRef.setValue(0);

            }
        });

    }

    private void getdata() {

        mRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    Map map = (Map) snapshot.getValue();

                    int spo2 = Integer.valueOf(map.get("spo2").toString());
                    int bpm = Integer.valueOf(map.get("bpm").toString());
                    spo2percentage.setText(String.valueOf(spo2));
                    bpmCount.setText(String.valueOf(bpm));

                    if(spo2 >=93 && bpm>50 && bpm<80 ){
                        spo2message.setText("Asthma attack : No risk");
                    } else if (spo2 >=90 && spo2 <93 && bpm>79 && bpm<110) {
                        spo2message.setText("Asthma attack : Medium risk");
                    } else if (spo2 <89 && spo2>0 && bpm>110) {
                        spo2message.setText("Asthma attack : High risk");
                    }else {
                        spo2message.setText("Please do a test");
                    }

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

                    progressBar1.setProgress(Math.round(temp));
                    textView1.setText(temp + " °C");

                    progressBar2.setProgress(Math.round(hum));
                    textView2.setText(hum + " %");

                    float x = (float) Math.round(gas);
                    float val = ((x / 1024) * 100);
                    Integer val_b = Math.round(val);
                    progressBar3.setProgress(val_b);
                    textView3.setText(String.valueOf(gas));

                    normal.setVisibility(View.VISIBLE);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    AlertDialog dialog = builder.create();

                    if(buttonalarm == 1 && a == 0){

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        // Setting Alert Dialog Title
                        alertDialogBuilder.setTitle("Alert !!!");
                        // Icon Of Alert Dialog
                        // Setting Alert Dialog Message
                        alertDialogBuilder.setMessage("Patient need help, may be he is in danger!");
                        alertDialogBuilder.setCancelable(false);

                        alertDialogBuilder.setPositiveButton("Yes to stop alarm", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                alart.setValue(0);
                                dialog.dismiss();
                                a = 0;
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        a=1;

                    }


                    if (gas > gmax || gas < gmin|| temp > tmax || temp < tmin || hum > hmax || hum < hmin) {

                        normal.setText("Danger! Room's environment is not normal.");

                    } else {
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