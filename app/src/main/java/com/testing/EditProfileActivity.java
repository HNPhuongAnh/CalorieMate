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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtTen, edtTuoi, edtChieuCao, edtCanNang, edtCanNangMucTieu;
    private RadioButton btnNam, btnNu, btnItVanDong, btnVanDongNhe, btnVanDongVua, btnVanDongNhieu;
    private Button btnEditProfile, btnBack;
    private DatabaseReference databaseReference;
    private String userId;

    private static final String TAG = "EditProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

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
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnBack = findViewById(R.id.btnBack);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference();
            loadUserData();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, FragProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadUserData() {
        databaseReference.child("ThongTinNguoiDung").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
                        } else {
                            Toast.makeText(EditProfileActivity.this, "No user data found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Database error: " + error.getMessage());
                        Toast.makeText(EditProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserData() {
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
            caloriesMucTieu = tdee - 500;
        } else {
            caloriesMucTieu = tdee + 500;
        }

        // Tính tổng lượng calo cần thâm hụt
        double tongCaloThamHut = Math.abs(weight - targetWeight) * 7700;

        // Tính thời gian dự kiến để đạt được mức cân nặng mục tiêu
        thoiGianDuKien = tongCaloThamHut / 500;

        DataClass dataClass = new DataClass(userId, ten, tuoi, chieucao, cannang, cannangmuctieu, gioitinh, vandong, bmi, tdee, bmr, luongNuoc, caloriesMucTieu, thoiGianDuKien);

        Log.d(TAG, "Updating data: " + dataClass.toString());

        databaseReference.child("ThongTinNguoiDung").child(userId)
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditProfileActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Cập nhật thông tin thất bại!", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to update data", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error: " + e.getMessage(), e);
                    }
                });
    }
}
