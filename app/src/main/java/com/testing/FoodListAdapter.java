package com.testing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FoodListAdapter extends ArrayAdapter<FoodItem> {

    public FoodListAdapter(Context context, List<FoodItem> foodItemList) {
        super(context, 0, foodItemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.food_item, parent, false);
        }

        FoodItem foodItem = getItem(position);

        TextView tvFoodName = convertView.findViewById(R.id.tvFoodName);
        /*TextView tvCalories = convertView.findViewById(R.id.tvCalories);*/

        if (foodItem != null) {
            tvFoodName.setText(foodItem.getName());
            /*tvCalories.setText(String.valueOf(foodItem.getCalories()));*/
        }

        return convertView;
    }
}
