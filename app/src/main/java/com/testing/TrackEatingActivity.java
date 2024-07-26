package com.testing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TrackEatingActivity extends AppCompatActivity {

    private TextView txtCaloriesMucTieu, txtCaloriesConLai;
    private LinearLayout layoutBreakfast, layoutLunch, layoutDinner, layoutSnacks;
    private Button btnAddBreakfast, btnAddLunch, btnAddDinner, btnAddSnack, btnBack, btnAddNewFood;
    private double targetCalories;
    private double remainingCalories;
    private String userId;
    private Map<String, Double> selectedFoodCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_eating);

        txtCaloriesMucTieu = findViewById(R.id.txtCaloriesMucTieu);
        txtCaloriesConLai = findViewById(R.id.txtCaloriesConLai);
        layoutBreakfast = findViewById(R.id.layoutBreakfast);
        layoutLunch = findViewById(R.id.layoutLunch);
        layoutDinner = findViewById(R.id.layoutDinner);
        layoutSnacks = findViewById(R.id.layoutSnacks);
        btnAddBreakfast = findViewById(R.id.btnAddBreakfast);
        btnAddLunch = findViewById(R.id.btnAddLunch);
        btnAddDinner = findViewById(R.id.btnAddDinner);
        btnAddSnack = findViewById(R.id.btnAddSnack);
        btnBack = findViewById(R.id.btnBack);
        btnAddNewFood = findViewById(R.id.btnAddNewFood);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        selectedFoodCalories = new HashMap<>();

        if (savedInstanceState != null) {
            remainingCalories = savedInstanceState.getDouble("remainingCalories");
            txtCaloriesConLai.setText(String.valueOf(remainingCalories));
            selectedFoodCalories = (Map<String, Double>) savedInstanceState.getSerializable("selectedFoodCalories");
            reloadFoodItems();
        } else {
            loadTargetCaloriesFromDatabase();
            duplicateFoodList();
            loadUserFoodList();
            listenForUserFoodListChanges();
        }

        btnAddBreakfast.setOnClickListener(view -> saveMealData());
        btnAddLunch.setOnClickListener(view -> saveMealData());
        btnAddDinner.setOnClickListener(view -> saveMealData());
        btnAddSnack.setOnClickListener(view -> saveMealData());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackEatingActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnAddNewFood.setOnClickListener(view -> openAddNewFoodActivity());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("remainingCalories", remainingCalories);
        outState.putSerializable("selectedFoodCalories", new HashMap<>(selectedFoodCalories));
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("TrackEatingPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("remainingCalories", (float) remainingCalories);
        editor.putString("selectedFoodCalories", new Gson().toJson(selectedFoodCalories));
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("TrackEatingPrefs", MODE_PRIVATE);
        remainingCalories = prefs.getFloat("remainingCalories", (float) targetCalories);
        txtCaloriesConLai.setText(String.valueOf(remainingCalories));

        String selectedFoodCaloriesJson = prefs.getString("selectedFoodCalories", "{}");
        selectedFoodCalories = new Gson().fromJson(selectedFoodCaloriesJson, new TypeToken<Map<String, Double>>() {}.getType());

        if (selectedFoodCalories.isEmpty()) {
            resetSelectedFoodCalories();
        } else {
            reloadFoodItems();
        }
    }

    private void loadTargetCaloriesFromDatabase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("ThongTinNguoiDung").child(userId);
        userRef.child("caloriesMucTieu").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    targetCalories = dataSnapshot.getValue(Double.class);
                    txtCaloriesMucTieu.setText(String.valueOf(targetCalories));
                    // Thay vì gán remainingCalories trực tiếp từ targetCalories,
                    // chúng ta sẽ tải nó từ cơ sở dữ liệu nếu tồn tại.
                    loadRemainingCaloriesFromDatabase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void loadRemainingCaloriesFromDatabase() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(new Date());

        DatabaseReference trackingUserRef = FirebaseDatabase.getInstance().getReference("TrackingUser").child(userId).child(formattedDate);
        trackingUserRef.child("caloriesConLai").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    remainingCalories = dataSnapshot.getValue(Double.class);
                } else {
                    // Nếu không có dữ liệu, mặc định remainingCalories là targetCalories
                    remainingCalories = targetCalories;
                    resetSelectedFoodCalories();
                }
                txtCaloriesConLai.setText(String.valueOf(remainingCalories));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    /*private void loadUserFoodList() {
        DatabaseReference userFoodListRef = FirebaseDatabase.getInstance().getReference("UserFoodList").child(userId + "_FoodList");

        userFoodListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Xóa tất cả các view trong layout
                layoutBreakfast.removeAllViews();
                layoutLunch.removeAllViews();
                layoutDinner.removeAllViews();
                layoutSnacks.removeAllViews();

                for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
                    String foodId = foodSnapshot.getKey();
                    String foodName = foodSnapshot.child("name").getValue(String.class);
                    double foodCalories = foodSnapshot.child("calories").getValue(Double.class);

                    // Thêm item thực phẩm vào layout tương ứng
                    addFoodItemToLayout(layoutBreakfast, foodId, foodName, foodCalories, "breakfast");
                    addFoodItemToLayout(layoutLunch, foodId, foodName, foodCalories, "lunch");
                    addFoodItemToLayout(layoutDinner, foodId, foodName, foodCalories, "dinner");
                    addFoodItemToLayout(layoutSnacks, foodId, foodName, foodCalories, "snack");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void addFoodItemToLayout(LinearLayout layout, String foodId, String foodName, double foodCalories, String mealType) {
        View foodItemView = getLayoutInflater().inflate(R.layout.food_item, null);
        CheckBox checkBox = foodItemView.findViewById(R.id.checkboxFoodItem);
        Button btnEditFood = foodItemView.findViewById(R.id.btnEditFood);

        checkBox.setText(foodName + " (" + foodCalories + " cal)");

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String key = mealType + "_" + foodName;
            if (isChecked) {
                selectedFoodCalories.put(key, foodCalories);
            } else {
                selectedFoodCalories.remove(key);
            }
            updateRemainingCalories();
        });

        btnEditFood.setOnClickListener(v -> openManageFoodActivity(foodId, foodName, foodCalories));

        layout.addView(foodItemView);
    }*/
    private void loadUserFoodList() {
        DatabaseReference userFoodListRef = FirebaseDatabase.getInstance().getReference("UserFoodList").child(userId + "_FoodList");

        userFoodListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Xóa tất cả các view trong layout
                layoutBreakfast.removeAllViews();
                layoutLunch.removeAllViews();
                layoutDinner.removeAllViews();
                layoutSnacks.removeAllViews();

                for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
                    String foodId = foodSnapshot.getKey();
                    String foodName = foodSnapshot.child("name").getValue(String.class);
                    double foodCalories = foodSnapshot.child("calories").getValue(Double.class);

                    // Thêm item thực phẩm vào layout tương ứng
                    addFoodItemToLayout(layoutBreakfast, foodId, foodName, foodCalories, "breakfast");
                    addFoodItemToLayout(layoutLunch, foodId, foodName, foodCalories, "lunch");
                    addFoodItemToLayout(layoutDinner, foodId, foodName, foodCalories, "dinner");
                    addFoodItemToLayout(layoutSnacks, foodId, foodName, foodCalories, "snack");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void addFoodItemToLayout(LinearLayout layout, String foodId, String foodName, double foodCalories, String mealType) {
        View foodItemView = getLayoutInflater().inflate(R.layout.food_item, null);
        CheckBox checkBox = foodItemView.findViewById(R.id.checkboxFoodItem);
        Button btnEditFood = foodItemView.findViewById(R.id.btnEditFood);

        checkBox.setText(foodName + " (" + foodCalories + " cal)");

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String key = mealType + "_" + foodName;
            if (isChecked) {
                selectedFoodCalories.put(key, foodCalories);
            } else {
                selectedFoodCalories.remove(key);
            }
            updateRemainingCalories();
        });

        btnEditFood.setOnClickListener(v -> openManageFoodActivity(foodId, foodName, foodCalories));

        layout.addView(foodItemView);
    }

    private void duplicateFoodList() {
        DatabaseReference userFoodListRef = FirebaseDatabase.getInstance().getReference("UserFoodList").child(userId + "_FoodList");

        userFoodListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists() || dataSnapshot.getChildrenCount() == 0) {
                    DatabaseReference foodListRef = FirebaseDatabase.getInstance().getReference("FoodList");

                    foodListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot foodListSnapshot) {
                            if (foodListSnapshot.exists()) {
                                for (DataSnapshot foodSnapshot : foodListSnapshot.getChildren()) {
                                    userFoodListRef.child(foodSnapshot.getKey()).setValue(foodSnapshot.getValue());
                                }
                                // Sau khi sao chép xong, tải lại danh sách thực phẩm
                                loadUserFoodList();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors here
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void listenForUserFoodListChanges() {
        DatabaseReference userFoodListRef = FirebaseDatabase.getInstance().getReference("UserFoodList").child(userId + "_FoodList");

        userFoodListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Mỗi khi có thay đổi, tải lại danh sách thực phẩm
                loadUserFoodList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

/*    private void saveMealData(String mealType) {
        DatabaseReference trackingUserRef = FirebaseDatabase.getInstance().getReference("TrackingUser").child(userId);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(new Date());

        trackingUserRef.child(formattedDate).child(mealType).setValue(selectedFoodCalories);

        trackingUserRef.child(formattedDate).child("caloriesConLai").setValue(remainingCalories);
    }*/
    private void saveMealData() {
        DatabaseReference trackingUserRef = FirebaseDatabase.getInstance().getReference("TrackingUser").child(userId);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(new Date());

        trackingUserRef.child(formattedDate).child("eatenFood").setValue(selectedFoodCalories);

        trackingUserRef.child(formattedDate).child("caloriesConLai").setValue(remainingCalories);
    }

    private void openManageFoodActivity(String foodId, String foodName, double foodCalories) {
        Intent intent = new Intent(this, ManageFoodActivity.class);
        intent.putExtra("foodId", foodId);
        intent.putExtra("foodName", foodName);
        intent.putExtra("foodCalories", foodCalories);
        startActivity(intent);
    }

    private void openAddNewFoodActivity() {
        Intent intent = new Intent(this, AddNewFoodActivity.class);
        startActivity(intent);
    }

    private void updateRemainingCalories() {
        remainingCalories = targetCalories;
        for (Double calories : selectedFoodCalories.values()) {
            remainingCalories -= calories;
        }
        txtCaloriesConLai.setText(String.valueOf(remainingCalories));
    }

    private void resetSelectedFoodCalories() {
        selectedFoodCalories.clear();
        layoutBreakfast.removeAllViews();
        layoutLunch.removeAllViews();
        layoutDinner.removeAllViews();
        layoutSnacks.removeAllViews();
    }

    /*private void reloadFoodItems() {
        for (Map.Entry<String, Double> entry : selectedFoodCalories.entrySet()) {
            String[] parts = entry.getKey().split("_");
            String mealType = parts[0];
            String foodName = parts[1];
            double foodCalories = entry.getValue();

            LinearLayout layout;
            switch (mealType) {
                case "breakfast":
                    layout = layoutBreakfast;
                    break;
                case "lunch":
                    layout = layoutLunch;
                    break;
                case "dinner":
                    layout = layoutDinner;
                    break;
                case "snack":
                    layout = layoutSnacks;
                    break;
                default:
                    continue;
            }

            View foodItemView = getLayoutInflater().inflate(R.layout.food_item, null);
            CheckBox checkBox = foodItemView.findViewById(R.id.checkboxFoodItem);
            Button btnEditFood = foodItemView.findViewById(R.id.btnEditFood);

            checkBox.setText(foodName + " (" + foodCalories + " cal)");
            checkBox.setChecked(true);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String key = mealType + "_" + foodName;
                if (isChecked) {
                    selectedFoodCalories.put(key, foodCalories);
                } else {
                    selectedFoodCalories.remove(key);
                }
                updateRemainingCalories();
            });

            btnEditFood.setOnClickListener(v -> openManageFoodActivity(null, foodName, foodCalories));

            layout.addView(foodItemView);
        }
    }*/
    private void reloadFoodItems() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(new Date());

        DatabaseReference trackingUserRef = FirebaseDatabase.getInstance().getReference("TrackingUser").child(userId).child(formattedDate).child("eatenFood");

        trackingUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                selectedFoodCalories.clear();
                for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
                    String key = foodSnapshot.getKey();
                    Double calories = foodSnapshot.getValue(Double.class);
                    selectedFoodCalories.put(key, calories);
                }
                loadUserFoodList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

}
