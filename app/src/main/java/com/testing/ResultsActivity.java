package com.testing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ResultsActivity extends AppCompatActivity {

    TextView txtTen, txtCanNangHienTai, txtCanNangMucTieu, txtCaloriesMucTieu, txtNgayDatMucTieu;
    RadioGroup radioGroupDifficulty;
    RadioButton radioEasy, radioMedium, radioHard, radioVeryHard;
    Button buttonHoanTat;

    private static final int EASY_DEFICIT = 220;
    private static final int MEDIUM_DEFICIT = 550;
    private static final int HARD_DEFICIT = 820;
    private static final int VERY_HARD_DEFICIT = 990;

    private double caloriesMucTieu;
    private int thoiGianDuKien;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        txtTen = findViewById(R.id.txtTen);
        txtCanNangHienTai = findViewById(R.id.txtCanNangHienTai);
        txtCanNangMucTieu = findViewById(R.id.txtCanNangMucTieu);
        txtCaloriesMucTieu = findViewById(R.id.txtCaloriesMucTieu);
        txtNgayDatMucTieu = findViewById(R.id.txtNgayDatMucTieu);
        radioGroupDifficulty = findViewById(R.id.radioGroupDifficulty);
        radioEasy = findViewById(R.id.radioEasy);
        radioMedium = findViewById(R.id.radioMedium);
        radioHard = findViewById(R.id.radioHard);
        radioVeryHard = findViewById(R.id.radioVeryHard);
        buttonHoanTat = findViewById(R.id.buttonHoanTat);

        // Get data from intent
        String ten = getIntent().getStringExtra("ten");
        String cannang = getIntent().getStringExtra("cannang");
        String cannangmuctieu = getIntent().getStringExtra("cannangmuctieu");
        caloriesMucTieu = getIntent().getDoubleExtra("caloriesMucTieu", 0.0);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Display data
        txtTen.setText(ten + ", kế hoạch của bạn đã sẵn sàng");
        txtCanNangHienTai.setText("Cân nặng hiện tại: " + cannang + " kg");
        txtCanNangMucTieu.setText("Cân nặng mục tiêu: " + cannangmuctieu + " kg");
        txtCaloriesMucTieu.setText("Calories mục tiêu: " + caloriesMucTieu);

        // Set radio button texts
        radioEasy.setText("Mức dễ: giảm " + EASY_DEFICIT + " calories/ngày");
        radioMedium.setText("Mức vừa: giảm " + MEDIUM_DEFICIT + " calories/ngày");
        radioHard.setText("Mức khó: giảm " + HARD_DEFICIT + " calories/ngày");
        radioVeryHard.setText("Mức cực khó: giảm " + VERY_HARD_DEFICIT + " calories/ngày");

        // Load thoiGianDuKien from database
        loadThoiGianDuKienFromDatabase();

        // Set default selection to medium difficulty
        radioMedium.setChecked(true);

        // Handle radio button selections
        radioGroupDifficulty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int caloriesDeficit = getCaloriesDeficit(checkedId);
                if (caloriesDeficit != 0) {
                    double newCaloriesMucTieu = caloriesMucTieu - caloriesDeficit;
                    txtCaloriesMucTieu.setText("Calories mục tiêu: " + newCaloriesMucTieu);
                    updateCaloriesMucTieuInDatabase(newCaloriesMucTieu);
                    calculateNgayDatMucTieu(caloriesDeficit);
                }
            }
        });

        buttonHoanTat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadThoiGianDuKienFromDatabase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://doan3-e4046-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("ThongTinNguoiDung").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    thoiGianDuKien = dataSnapshot.child("thoiGianDuKien").getValue(Integer.class);
                    calculateNgayDatMucTieu(getCaloriesDeficit(radioGroupDifficulty.getCheckedRadioButtonId()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void updateCaloriesMucTieuInDatabase(double newCaloriesMucTieu) {
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://doan3-e4046-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("ThongTinNguoiDung").child(userId);
        userRef.child("caloriesMucTieu").setValue(newCaloriesMucTieu);
    }

    private void calculateNgayDatMucTieu(int caloriesDeficit) {
        if (caloriesDeficit != 0 && thoiGianDuKien > 0) {
            // Tính lại thời gian dự kiến
            int ngayDatMucTieu = (int) Math.ceil(thoiGianDuKien / (double) caloriesDeficit);

            // Tính ngày cụ thể sau số ngày dự kiến
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, ngayDatMucTieu);
            Date ngayDatMucTieuDate = calendar.getTime();

            // Định dạng ngày
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String ngayThangNam = sdf.format(ngayDatMucTieuDate);

            // Hiển thị ngày
            txtNgayDatMucTieu.setText("Ngày đạt được mục tiêu: " + ngayThangNam);

            // Cập nhật thời gian dự kiến trong cơ sở dữ liệu
            updateThoiGianDuKienInDatabase(ngayDatMucTieu);
        }
    }

    private void updateThoiGianDuKienInDatabase(int ngayDatMucTieu) {
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://doan3-e4046-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("ThongTinNguoiDung").child(userId);
        userRef.child("thoiGianDuKien").setValue(ngayDatMucTieu);
    }

    private int getCaloriesDeficit(int checkedId) {
        int caloriesDeficit = 0;
        if (checkedId == R.id.radioEasy) {
            caloriesDeficit = EASY_DEFICIT;
        } else if (checkedId == R.id.radioMedium) {
            caloriesDeficit = MEDIUM_DEFICIT;
        } else if (checkedId == R.id.radioHard) {
            caloriesDeficit = HARD_DEFICIT;
        } else if (checkedId == R.id.radioVeryHard) {
            caloriesDeficit = VERY_HARD_DEFICIT;
        }
        return caloriesDeficit;
    }
}
