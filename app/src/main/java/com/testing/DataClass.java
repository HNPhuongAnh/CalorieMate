package com.testing;

public class DataClass {
    private String userId;
    private String ten;
    private String tuoi;
    private String chieucao;
    private String cannang;
    private String cannangmuctieu;
    private String gioitinh;
    private String vandong;
    private double bmi;
    private double tdee;
    private double bmr;
    private double luongNuoc;
    private double caloriesMucTieu;
    private double thoiGianDuKien;

    public DataClass() {
        // Default constructor required for calls to DataSnapshot.getValue(DataClass.class)
    }

    public DataClass(String userId, String ten, String tuoi, String chieucao, String cannang, String cannangmuctieu, String gioitinh, String vandong,
                     double bmi, double tdee, double bmr, double luongNuoc, double caloriesMucTieu, double thoiGianDuKien) {
        this.userId = userId;
        this.ten = ten;
        this.tuoi = tuoi;
        this.chieucao = chieucao;
        this.cannang = cannang;
        this.cannangmuctieu = cannangmuctieu;
        this.gioitinh = gioitinh;
        this.vandong = vandong;
        this.bmi = bmi;
        this.tdee = tdee;
        this.bmr = bmr;
        this.luongNuoc = luongNuoc;
        this.caloriesMucTieu = caloriesMucTieu;
        this.thoiGianDuKien = thoiGianDuKien;
    }

    // Getter and setter methods for all fields
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getTuoi() {
        return tuoi;
    }

    public void setTuoi(String tuoi) {
        this.tuoi = tuoi;
    }

    public String getChieucao() {
        return chieucao;
    }

    public void setChieucao(String chieucao) {
        this.chieucao = chieucao;
    }

    public String getCannang() {
        return cannang;
    }

    public void setCannang(String cannang) {
        this.cannang = cannang;
    }

    public String getCannangmuctieu() {
        return cannangmuctieu;
    }

    public void setCannangmuctieu(String cannangmuctieu) {
        this.cannangmuctieu = cannangmuctieu;
    }

    public String getGioitinh() {
        return gioitinh;
    }

    public void setGioitinh(String gioitinh) {
        this.gioitinh = gioitinh;
    }

    public String getVandong() {
        return vandong;
    }

    public void setVandong(String vandong) {
        this.vandong = vandong;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public double getTdee() {
        return tdee;
    }

    public void setTdee(double tdee) {
        this.tdee = tdee;
    }

    public double getBmr() {
        return bmr;
    }

    public void setBmr(double bmr) {
        this.bmr = bmr;
    }

    public double getLuongNuoc() {
        return luongNuoc;
    }

    public void setLuongNuoc(double luongNuoc) {
        this.luongNuoc = luongNuoc;
    }

    public double getCaloriesMucTieu() {
        return caloriesMucTieu;
    }

    public void setCaloriesMucTieu(double caloriesMucTieu) {
        this.caloriesMucTieu = caloriesMucTieu;
    }

    public double getThoiGianDuKien() {
        return thoiGianDuKien;
    }

    public void setThoiGianDuKien(double thoiGianDuKien) {
        this.thoiGianDuKien = thoiGianDuKien;
    }

    @Override
    public String toString() {
        return "DataClass{" +
                "userId='" + userId + '\'' +
                ", ten='" + ten + '\'' +
                ", tuoi='" + tuoi + '\'' +
                ", chieucao='" + chieucao + '\'' +
                ", cannang='" + cannang + '\'' +
                ", cannangmuctieu='" + cannangmuctieu + '\'' +
                ", gioitinh='" + gioitinh + '\'' +
                ", vandong='" + vandong + '\'' +
                ", bmi=" + bmi +
                ", tdee=" + tdee +
                ", bmr=" + bmr +
                ", luongNuoc=" + luongNuoc +
                ", caloriesMucTieu=" + caloriesMucTieu +
                ", thoiGianDuKien=" + thoiGianDuKien +
                '}';
    }
}
