package com.testing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CaloriesCaculator extends AppCompatActivity {

    EditText edtTen, edtTuoi, edtChieuCao, edtCanNang, edtCanNangMucTieu;
    RadioButton btnNam, btnNu, btnItVanDong, btnVanDongNhe, btnVanDongVua, btnVanDongNhieu;
    Button btnBoQua, btnHoanThanh;

    private static final String TAG = "CaloriesCaculator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calories_caculator);

        edtTen = findViewById(R.id.edtTen);
        edtTuoi = findViewById(R.id.edtTuoi);
        edtChieuCao = findViewById(R.id.edtChieuCao);
        edtCanNang = findViewById(R.id.edtCanNang);
        edtCanNangMucTieu = findViewById(R.id.edtCanNangMucTieu);
        btnNam = findViewById(R.id.btnNam);
        btnNu = findViewById(R.id.btnNu);
        btnItVanDong = findViewById(R.id.btnItVanDong);
        btnVanDongNhe = findViewById(R.id.btnVanDongNhe);
        btnVanDongVua = findViewById(R.id.btnVanDongVua);
        btnVanDongNhieu = findViewById(R.id.btnVanDongNhieu);
        btnBoQua = findViewById(R.id.btnBoQua);
        btnHoanThanh = findViewById(R.id.btnHoanThanh);

        btnHoanThanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
                Intent intent = new Intent(CaloriesCaculator.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnBoQua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndSkip();
            }
        });

        // Tải dữ liệu người dùng lên nếu đã có
        loadUserData();
    }

    private void loadUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://doan3-e4046-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("ThongTinNguoiDung").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataClass data = snapshot.getValue(DataClass.class);
                    if (data != null) {
                        edtTen.setText(data.getTen());
                        edtTuoi.setText(data.getTuoi());
                        edtChieuCao.setText(data.getChieucao());
                        edtCanNang.setText(data.getCannang());
                        edtCanNangMucTieu.setText(data.getCannangmuctieu());
                        if (data.getGioitinh().equals("Nam")) {
                            btnNam.setChecked(true);
                        } else {
                            btnNu.setChecked(true);
                        }
                        switch (data.getVandong()) {
                            case "Ít vận động":
                                btnItVanDong.setChecked(true);
                                break;
                            case "Vận động nhẹ":
                                btnVanDongNhe.setChecked(true);
                                break;
                            case "Vận động vừa":
                                btnVanDongVua.setChecked(true);
                                break;
                            case "Vận động nhiều":
                                btnVanDongNhieu.setChecked(true);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
                Toast.makeText(CaloriesCaculator.this, "Có lỗi xảy ra, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAndSkip() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://doan3-e4046-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("ThongTinNguoiDung").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    // Thông tin người dùng đã có trong database, cho phép bỏ qua
                    Intent intent = new Intent(CaloriesCaculator.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Thông tin người dùng chưa có trong database, hiển thị thông báo
                    Toast.makeText(CaloriesCaculator.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
                Toast.makeText(CaloriesCaculator.this, "Có lỗi xảy ra, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadData() {
        String ten = edtTen.getText().toString();
        String tuoi = edtTuoi.getText().toString();
        String chieucao = edtChieuCao.getText().toString();
        String cannang = edtCanNang.getText().toString();
        String cannangmuctieu = edtCanNangMucTieu.getText().toString();
        String gioitinh = btnNam.isChecked() ? "Nam" : "Nữ";
        String vandong = "";

        if (btnItVanDong.isChecked()) {
            vandong = "Ít vận động";
        } else if (btnVanDongNhe.isChecked()) {
            vandong = "Vận động nhẹ";
        } else if (btnVanDongVua.isChecked()) {
            vandong = "Vận động vừa";
        } else if (btnVanDongNhieu.isChecked()) {
            vandong = "Vận động nhiều";
        }

        if (ten.isEmpty() || tuoi.isEmpty() || chieucao.isEmpty() || cannang.isEmpty() || cannangmuctieu.isEmpty() || vandong.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        double heightInMeters = Double.parseDouble(chieucao) / 100;
        double weight = Double.parseDouble(cannang);
        double targetWeight = Double.parseDouble(cannangmuctieu);
        int age = Integer.parseInt(tuoi);
        double bmr, tdee, bmi, luongNuoc, caloriesMucTieu, thoiGianDuKien;

        // Tính BMI
        bmi = weight / (heightInMeters * heightInMeters);

        // Tính BMR
        if (gioitinh.equals("Nam")) {
            bmr = 10 * weight + 6.25 * Double.parseDouble(chieucao) - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * Double.parseDouble(chieucao) - 5 * age - 161;
        }

        // Tính TDEE
        switch (vandong) {
            case "Ít vận động":
                tdee = bmr * 1.2;
                break;
            case "Vận động nhẹ":
                tdee = bmr * 1.375;
                break;
            case "Vận động vừa":
                tdee = bmr * 1.55;
                break;
            case "Vận động nhiều":
                tdee = bmr * 1.725;
                break;
            default:
                tdee = bmr * 1.375;
                break;
        }

        // Tính lượng nước cần uống (theo công thức 0.033 * cân nặng tính bằng kg)
        luongNuoc = weight * 0.033;

        // Tính số calories cần ăn để đạt được mức cân mục tiêu (giảm hoặc tăng 500 calories mỗi ngày)
        if (weight > targetWeight) {
            caloriesMucTieu = tdee;
        }else if (weight == targetWeight) {
                caloriesMucTieu = tdee;
        } else {
            caloriesMucTieu = tdee + 200;
        }

        // Tính tổng lượng calo cần thâm hụt
        double tongCaloThamHut = Math.abs(weight - targetWeight) * 7700;

        // Tính thời gian dự kiến để đạt được mức cân nặng mục tiêu
        thoiGianDuKien = tongCaloThamHut;

        DataClass dataClass = new DataClass(userId, ten, tuoi, chieucao, cannang, cannangmuctieu, gioitinh, vandong, bmi, tdee, bmr, luongNuoc, caloriesMucTieu, thoiGianDuKien);

        Log.d(TAG, "Uploading data: " + dataClass.toString());

        FirebaseDatabase.getInstance("https://doan3-e4046-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("ThongTinNguoiDung").child(userId)
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CaloriesCaculator.this, "Đã lưu!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CaloriesCaculator.this, ResultsActivity.class);
                            intent.putExtra("ten", ten);
                            intent.putExtra("cannang", cannang);
                            intent.putExtra("cannangmuctieu", cannangmuctieu);
                            intent.putExtra("caloriesMucTieu", caloriesMucTieu);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(CaloriesCaculator.this, "Failed to save data!", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to save data", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CaloriesCaculator.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error: " + e.getMessage(), e);
                    }
                });
    }

}
