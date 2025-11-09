package com.example.myapplication.dto;

public class SavingsRequest {
    public String userId;
    public String groupName;
    public Float targetAmount;
    public Float savedAmount;

    public SavingsRequest(String userId, String groupName, Float targetAmount, Float savedAmount) {
        this.userId = userId;
        this.groupName = groupName;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
    }
}
