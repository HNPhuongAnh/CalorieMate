package com.testing;

public class FoodItem {
    private String id;
    private String name;
    private double calories;

    public FoodItem() {
        // Default constructor required for calls to DataSnapshot.getValue(FoodItem.class)
    }

    public FoodItem(String id, String name, double calories) {
        this.id = id;
        this.name = name;
        this.calories = calories;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getCalories() {
        return calories;
    }
}
