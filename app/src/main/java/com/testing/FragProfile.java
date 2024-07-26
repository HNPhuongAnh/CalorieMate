package com.testing;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FragProfile extends Fragment {

    private TextView tvTen, tvTuoi, tvChieuCao, tvCanNang, tvCanNangMucTieu, tvGioiTinh, tvVanDong, tvBmi, tvTdee, tvBmr, tvLuongNuoc, tvCaloriesMucTieu, tvThoiGianDuKien, tvEmail;
    private ImageView imgProfile;
    private Button btnEditProfile, btnLogout;
    private DatabaseReference userRef, googleAuthRef;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag_profile, container, false);

        tvTen = view.findViewById(R.id.tvTen);
        tvTuoi = view.findViewById(R.id.tvTuoi);
        tvChieuCao = view.findViewById(R.id.tvChieuCao);
        tvCanNang = view.findViewById(R.id.tvCanNang);
        tvCanNangMucTieu = view.findViewById(R.id.tvCanNangMucTieu);
        tvGioiTinh = view.findViewById(R.id.tvGioiTinh);
        tvVanDong = view.findViewById(R.id.tvVanDong);
        tvBmi = view.findViewById(R.id.tvBmi);
        tvTdee = view.findViewById(R.id.tvTdee);
        tvBmr = view.findViewById(R.id.tvBmr);
        tvLuongNuoc = view.findViewById(R.id.tvLuongNuoc);
        tvCaloriesMucTieu = view.findViewById(R.id.tvCaloriesMucTieu);
        tvThoiGianDuKien = view.findViewById(R.id.tvThoiGianDuKien);
        tvEmail = view.findViewById(R.id.tvEmail);
        imgProfile = view.findViewById(R.id.imgProfile);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnLogout);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("ThongTinNguoiDung").child(userId);
            googleAuthRef = FirebaseDatabase.getInstance().getReference("GoogleAuth").child(userId);
            loadUserData();
            loadGoogleAuthData();
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), SignIn.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataClass data = snapshot.getValue(DataClass.class);
                    if (data != null) {
                        tvTen.setText(data.getTen());
                        tvTuoi.setText(data.getTuoi());
                        tvChieuCao.setText(data.getChieucao());
                        tvCanNang.setText(data.getCannang());
                        tvCanNangMucTieu.setText(data.getCannangmuctieu());
                        tvGioiTinh.setText(data.getGioitinh());
                        tvVanDong.setText(data.getVandong());
                        tvBmi.setText(String.valueOf(data.getBmi()));
                        tvTdee.setText(String.valueOf(data.getTdee()));
                        tvBmr.setText(String.valueOf(data.getBmr()));
                        tvLuongNuoc.setText(String.valueOf(data.getLuongNuoc()));
                        tvCaloriesMucTieu.setText(String.valueOf(data.getCaloriesMucTieu()));
                        tvThoiGianDuKien.setText(String.valueOf(data.getThoiGianDuKien()));
                    }
                } else {
                    Toast.makeText(getContext(), "No user data found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGoogleAuthData() {
        googleAuthRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String email = snapshot.child("email").getValue(String.class);
                    String photoUrl = snapshot.child("profile").getValue(String.class);
                    String gender = snapshot.child("gioitinh").getValue(String.class);

                    tvEmail.setText(email);
                    if (photoUrl != null) {
                        Picasso.get()
                                .load(photoUrl)
                                .resize(100, 100) // Ensure image fits within the desired size
                                .centerCrop()     // Ensure image scales properly
                                .into(imgProfile);
                    } else {
                        if ("Nữ".equals(gender)) {
                            imgProfile.setImageResource(R.drawable.female_default_image); // Female default image
                        } else if ("Nam".equals(gender)) {
                            imgProfile.setImageResource(R.drawable.male_default_image); // Male default image
                        } else {
                            imgProfile.setImageResource(R.drawable.female_default_image); // Default image if gender is not specified
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "No GoogleAuth data found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load GoogleAuth data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
