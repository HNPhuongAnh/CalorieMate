package com.testing;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MethodDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method_detail);

        TextView tvMethodDetailName = findViewById(R.id.tvMethodDetailName);
        TextView tvMethodDetailDescription = findViewById(R.id.tvMethodDetailDescription);

        String methodName = getIntent().getStringExtra("method_name");
        String methodDetails = getIntent().getStringExtra("method_details");

        tvMethodDetailName.setText(methodName);
        tvMethodDetailDescription.setText(methodDetails);
    }
}
