package com.testing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TrackDrinkingActivity extends AppCompatActivity {

    private TextView tvGoalWater, tvWaterConsumed;
    private CircularProgressIndicator progressBarWater;
    private Button btnAdd100ml, btnAdd200ml, btnAdd300ml, btnAdd400ml, btnAdd500ml, btnReset, btnBack;
    private double goalWater;  // Goal in milliliters, will be fetched from database
    private int consumedWater = 0;
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_drinking);

        tvGoalWater = findViewById(R.id.tvGoalWater);
        tvWaterConsumed = findViewById(R.id.tvWaterConsumed);
        progressBarWater = findViewById(R.id.progressBarWater);
        btnAdd100ml = findViewById(R.id.btnAdd100ml);
        btnAdd200ml = findViewById(R.id.btnAdd200ml);
        btnAdd300ml = findViewById(R.id.btnAdd300ml);
        btnAdd400ml = findViewById(R.id.btnAdd400ml);
        btnAdd500ml = findViewById(R.id.btnAdd500ml);
        btnReset = findViewById(R.id.btnReset);
        btnBack = findViewById(R.id.btnBack);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        fetchGoalWater();
        fetchConsumedWater();

        btnAdd100ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(100);
            }
        });

        btnAdd200ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(200);
            }
        });

        btnAdd300ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(300);
            }
        });

        btnAdd400ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(400);
            }
        });

        btnAdd500ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(500);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetWater();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackDrinkingActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchGoalWater() {
        databaseReference.child("ThongTinNguoiDung").child(userId).child("luongNuoc")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            double goalWaterLiters = snapshot.getValue(Double.class);
                            goalWater = Math.round(goalWaterLiters * 1000 * 100.0) / 100.0; // Convert to ml and round to 2 decimal places
                            tvGoalWater.setText("Goal: " + goalWater + " ml");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    private void fetchConsumedWater() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(new Date());

        databaseReference.child("TrackingUser").child(userId).child(formattedDate).child("luongNuocDaUong")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            consumedWater = snapshot.getValue(Integer.class);
                            updateUI();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    private void addWater(int amount) {
        consumedWater += amount;
        updateUI();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(new Date());

        databaseReference.child("TrackingUser").child(userId).child(formattedDate).child("luongNuocDaUong")
                .setValue(consumedWater);
    }

    private void resetWater() {
        consumedWater = 0;
        updateUI();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(new Date());

        databaseReference.child("TrackingUser").child(userId).child(formattedDate).child("luongNuocDaUong")
                .setValue(consumedWater);
    }

    private void updateUI() {
        tvWaterConsumed.setText(consumedWater + " ml");
        int progress = (int) ((consumedWater / goalWater) * 100);
        progressBarWater.setProgress(progress);
    }
}
