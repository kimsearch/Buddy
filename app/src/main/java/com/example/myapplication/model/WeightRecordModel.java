package com.example.myapplication.model;

public class WeightRecordModel {

    private Long memberId;   // 회원 ID
    private float weight;    // 몸무게
    private String recordDate;  // ✅ 변수명도 Date로 통일

    public WeightRecordModel() {}

    public WeightRecordModel(Long memberId, float weight, String recordDate) {
        this.memberId = memberId;
        this.weight = weight;
        this.recordDate = recordDate; // ✅ 수정됨
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

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    @Override
    public String toString() {
        return "WeightRecordModel{" +
                "memberId=" + memberId +
                ", weight=" + weight +
                ", recordDate='" + recordDate + '\'' +
                '}';
    }
}
