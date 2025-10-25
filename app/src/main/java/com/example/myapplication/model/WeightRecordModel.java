package com.example.myapplication.model;

public class WeightRecordModel {

    private Long memberId;   // 회원 ID
    private float weight;    // 몸무게
    private String recordData;     // 기록 날짜

    public WeightRecordModel() {}

    public WeightRecordModel(Long memberId, float weight, String date) {
        this.memberId = memberId;
        this.weight = weight;
        this.recordData = recordData;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getDate() {
        return recordData;
    }

    public void setDate(String recordData) {
        this.recordData = recordData;
    }

    @Override
    public String toString() {
        return "WeightRecordModel{" +
                "memberId=" + memberId +
                ", weight=" + weight +
                ", date='" + recordData + '\'' +
                '}';
    }
}
