package com.example.atmdevice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class Calibration extends AppCompatActivity {

    EditText tempmax, tempmin, hummax, hummin, gasmax, gasmin;
    Button temp, hum, gas;
    DatabaseReference rootRef, tmaxRef, tminRef, hmaxRef, hminRef, gmaxRef, gminRef;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference();

        getdata();

        tempmax = findViewById(R.id.tempmax);
        tempmin = findViewById(R.id.tempmin);
        hummax = findViewById(R.id.hummax);
        hummin = findViewById(R.id.hummin);
        gasmax = findViewById(R.id.gasmax);
        gasmin = findViewById(R.id.gasmin);

        temp = findViewById(R.id.calibtemp);
        hum = findViewById(R.id.calibhum);
        gas = findViewById(R.id.calibgas);

        rootRef = FirebaseDatabase.getInstance().getReference();

        // Database reference pointing to demo node
        tmaxRef = rootRef.child("tempmax");
        tminRef = rootRef.child("tempmin");

        hmaxRef = rootRef.child("hummax");
        hminRef = rootRef.child("hummin");

        gmaxRef = rootRef.child("gasmax");
        gminRef = rootRef.child("gasmin");

        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Float tmax = Float.valueOf(tempmax.getText().toString());
                Float tmin = Float.valueOf(tempmin.getText().toString());

                tmaxRef.setValue(tmax);
                tminRef.setValue(tmin);


            }
        });
        hum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Float hmax = Float.valueOf(hummax.getText().toString());
                Float hmin = Float.valueOf(hummin.getText().toString());

                hmaxRef.setValue(hmax);
                hminRef.setValue(hmin);

            }
        });
        gas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Float gmax = Float.valueOf(gasmax.getText().toString());
                Float gmin = Float.valueOf(gasmin.getText().toString());

                gmaxRef.setValue(gmax);
                gminRef.setValue(gmin);
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

                    Float tmax = Float.valueOf(map.get("tempmax").toString());
                    Float hmax = Float.valueOf(map.get("hummax").toString());
                    Integer gmax = Integer.valueOf(map.get("gasmax").toString());

                    Float tmin = Float.valueOf(map.get("tempmin").toString());
                    Float hmin = Float.valueOf(map.get("hummin").toString());
                    Integer gmin = Integer.valueOf(map.get("gasmin").toString());


                    tempmax.setText(Float.toString(tmax));
                    tempmin.setText(Float.toString(tmin));

                    hummax.setText(Float.toString(hmax));
                    hummin.setText(Float.toString(hmin));

                    gasmax.setText(String.valueOf(gmax));
                    gasmin.setText(String.valueOf(gmin));


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(Calibration.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

}