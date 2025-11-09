package com.example.myapplication.dto;

public class BurnedRequest {
    public String userId;
    public String groupName;
    public Float targetKcal;
    public Float burnedKcal;

    public BurnedRequest(String userId, String groupName, Float targetKcal, Float burnedKcal) {
        this.userId = userId;
        this.groupName = groupName;
        this.targetKcal = targetKcal;
        this.burnedKcal = burnedKcal;
    }
}
