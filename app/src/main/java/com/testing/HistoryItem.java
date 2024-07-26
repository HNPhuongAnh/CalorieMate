package com.testing;

public class HistoryItem {

    private String date;
    private String foods;
    private int waterConsumed;

    public HistoryItem(String date, String foods, int waterConsumed) {
        this.date = date;
        this.foods = foods;
        this.waterConsumed = waterConsumed;
    }

    public String getDate() {
        return date;
    }

    public String getFoods() {
        return foods;
    }

    public int getWaterConsumed() {
        return waterConsumed;
    }
}
