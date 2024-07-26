package com.testing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerViewHistory;
    private HistoryAdapter historyAdapter;
    private ArrayList<HistoryItem> historyList;
    private DatabaseReference databaseReference;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        historyList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(historyList);
        recyclerViewHistory.setAdapter(historyAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference();
            fetchHistoryData();
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    /*private void fetchHistoryData() {
        databaseReference.child("TrackingUser").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            historyList.clear(); // Clear the list before adding new data
                            for (DataSnapshot yearSnapshot : snapshot.getChildren()) {
                                for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                                    for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                                        String date = daySnapshot.getKey() + "/" + monthSnapshot.getKey() + "/" + yearSnapshot.getKey();
                                        Integer waterConsumed = daySnapshot.child("luongNuocDaUong").getValue(Integer.class);

                                        StringBuilder foodsBuilder = new StringBuilder();
                                        for (DataSnapshot mealSnapshot : daySnapshot.getChildren()) {
                                            if (!mealSnapshot.getKey().equals("luongNuocDaUong") && !mealSnapshot.getKey().equals("caloriesConLai")) {
                                                for (DataSnapshot foodSnapshot : mealSnapshot.getChildren()) {
                                                    foodsBuilder.append(foodSnapshot.getKey()).append(": ").append(foodSnapshot.getValue()).append("\n");
                                                }
                                                foodsBuilder.setLength(foodsBuilder.length() - 2); // Remove the last comma and space
                                                foodsBuilder.append("\n");
                                            }
                                        }

                                        String foods = foodsBuilder.toString().trim();

                                        if (date != null && waterConsumed != null) {
                                            historyList.add(new HistoryItem(date, foods, waterConsumed));
                                        }
                                    }
                                }
                            }
                            historyAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "No history found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load history.", Toast.LENGTH_SHORT).show();
                    }
                });
    }*/
    private void fetchHistoryData() {
        databaseReference.child("TrackingUser").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            historyList.clear(); // Clear the list before adding new data
                            for (DataSnapshot yearSnapshot : snapshot.getChildren()) {
                                for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                                    for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                                        boolean hasFoodData = false;
                                        boolean hasWaterData = false;
                                        String date = daySnapshot.getKey() + "/" + monthSnapshot.getKey() + "/" + yearSnapshot.getKey();
                                        Integer waterConsumed = daySnapshot.child("luongNuocDaUong").getValue(Integer.class);

                                        StringBuilder foodsBuilder = new StringBuilder();
                                        for (DataSnapshot mealSnapshot : daySnapshot.getChildren()) {
                                            if (!mealSnapshot.getKey().equals("luongNuocDaUong") && !mealSnapshot.getKey().equals("caloriesConLai")) {
                                                for (DataSnapshot foodSnapshot : mealSnapshot.getChildren()) {
                                                    foodsBuilder.append(foodSnapshot.getKey()).append(": ").append(foodSnapshot.getValue()).append("\n");
                                                }
                                                hasFoodData = true;
                                                foodsBuilder.setLength(foodsBuilder.length() - 2); // Remove the last comma and space
                                                foodsBuilder.append("\n");
                                            }
                                            else if (!mealSnapshot.getKey().equals("caloriesConLai")) {
                                                for (DataSnapshot foodSnapshot : mealSnapshot.getChildren()) {
                                                    foodsBuilder.append(foodSnapshot.getKey()).append(": ").append(foodSnapshot.getValue()).append("\n");
                                                }
                                                hasWaterData = true;
                                            }
                                        }

                                        String foods = foodsBuilder.toString().trim();
                                        if (!hasFoodData) {
                                            foods = "Chưa có dữ liệu";
                                        }
                                        if (!hasWaterData) {
                                            waterConsumed = 0; // Or any other default value
                                        }
                                        if (date != null && waterConsumed != null) {
                                            historyList.add(new HistoryItem(date, foods, waterConsumed));
                                        }
                                    }
                                }
                            }


                            historyAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "No history found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load history.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
