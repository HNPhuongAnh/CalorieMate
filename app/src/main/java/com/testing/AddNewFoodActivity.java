package com.testing;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNewFoodActivity extends AppCompatActivity {

    private EditText edtFoodName, edtFoodCalories;
    private Button btnSaveFood, btnCancel;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_food);

        edtFoodName = findViewById(R.id.edtFoodName);
        edtFoodCalories = findViewById(R.id.edtFoodCalories);
        btnSaveFood = findViewById(R.id.btnSaveFood);
        btnCancel = findViewById(R.id.btnCancel);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        btnSaveFood.setOnClickListener(view -> saveFood());
        btnCancel.setOnClickListener(view -> finish());
    }

/*    private void saveFood() {
        String foodName = edtFoodName.getText().toString().trim();
        String caloriesStr = edtFoodCalories.getText().toString().trim();

        if (foodName.isEmpty() || caloriesStr.isEmpty()) {
            Toast.makeText(AddNewFoodActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double foodCalories;
        try {
            foodCalories = Double.parseDouble(caloriesStr);
        } catch (NumberFormatException e) {
            Toast.makeText(AddNewFoodActivity.this, "Invalid calories", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userFoodListRef = FirebaseDatabase.getInstance().getReference("UserFoodList").child(userId + "_FoodList");
        String foodId = userFoodListRef.push().getKey(); // Generate a unique ID for the new food item

        if (foodId != null) {
            userFoodListRef.child(foodId).setValue(new FoodItem(foodName, foodCalories))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddNewFoodActivity.this, "Food added successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddNewFoodActivity.this, "Failed to add food", Toast.LENGTH_SHORT).show());
        }
    }*/
private void saveFood() {
    String foodName = edtFoodName.getText().toString().trim();
    String caloriesStr = edtFoodCalories.getText().toString().trim();

    if (foodName.isEmpty() || caloriesStr.isEmpty()) {
        Toast.makeText(AddNewFoodActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
        return;
    }

    double foodCalories;
    try {
        foodCalories = Double.parseDouble(caloriesStr);
    } catch (NumberFormatException e) {
        Toast.makeText(AddNewFoodActivity.this, "Invalid calories", Toast.LENGTH_SHORT).show();
        return;
    }

    DatabaseReference userFoodListRef = FirebaseDatabase.getInstance().getReference("UserFoodList").child(userId + "_FoodList");

    // Use foodName as the ID
    String foodId = foodName.replaceAll("[^a-zA-Z0-9]", "_"); // Replace non-alphanumeric characters with underscores

    userFoodListRef.child(foodId).setValue(new FoodItem(foodName, foodCalories))
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(AddNewFoodActivity.this, "Food added successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            })
            .addOnFailureListener(e -> Toast.makeText(AddNewFoodActivity.this, "Failed to add food", Toast.LENGTH_SHORT).show());
}


    // Create a class to represent the food item
    private static class FoodItem {
        public String name;
        public double calories;

        public FoodItem() {
            // Default constructor required for calls to DataSnapshot.getValue(FoodItem.class)
        }

        public FoodItem(String name, double calories) {
            this.name = name;
            this.calories = calories;
        }
    }
}
