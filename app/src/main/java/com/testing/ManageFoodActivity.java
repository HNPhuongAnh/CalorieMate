package com.testing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManageFoodActivity extends AppCompatActivity {

    private EditText edtFoodName, edtFoodCalories;
    private Button btnSave, btnDelete, btnBack;
    private String userId;
    private String foodId;
    private String foodName;
    private double foodCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_food);

        edtFoodName = findViewById(R.id.edtFoodName);
        edtFoodCalories = findViewById(R.id.edtFoodCalories);
        btnSave = findViewById(R.id.btnSaveFood);
        btnDelete = findViewById(R.id.btnDeleteFood);
        btnBack = findViewById(R.id.btnBack);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Intent intent = getIntent();
        foodId = intent.getStringExtra("foodId");
        foodName = intent.getStringExtra("foodName");
        foodCalories = intent.getDoubleExtra("foodCalories", 0.0);

        edtFoodName.setText(foodName);
        edtFoodCalories.setText(String.valueOf(foodCalories));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFoodItem();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFoodItem();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveFoodItem() {
        String newName = edtFoodName.getText().toString();
        double newCalories = Double.parseDouble(edtFoodCalories.getText().toString());

        DatabaseReference userFoodListRef = FirebaseDatabase.getInstance().getReference("UserFoodList").child(userId + "_FoodList").child(foodId);
        userFoodListRef.child("name").setValue(newName);
        userFoodListRef.child("calories").setValue(newCalories);

        Toast.makeText(this, "Food item updated", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void deleteFoodItem() {
        DatabaseReference userFoodListRef = FirebaseDatabase.getInstance().getReference("UserFoodList").child(userId + "_FoodList").child(foodId);
        userFoodListRef.removeValue();

        Toast.makeText(this, "Food item deleted", Toast.LENGTH_SHORT).show();
        finish();
    }
}
