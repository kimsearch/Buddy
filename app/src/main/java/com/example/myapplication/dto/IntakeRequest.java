package com.example.myapplication.dto;

public class IntakeRequest {
    public String userId;
    public String groupName;
    public Float targetKcal;
    public Float consumedKcal;

    public IntakeRequest(String userId, String groupName, Float targetKcal, Float consumedKcal) {
        this.userId = userId;
        this.groupName = groupName;
        this.targetKcal = targetKcal;
        this.consumedKcal = consumedKcal;
    }
}
