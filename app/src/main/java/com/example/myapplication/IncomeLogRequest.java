package com.example.myapplication;

public class IncomeLogRequest {
    private Long groupId;
    private Long memberId;
    private int amount;

    public IncomeLogRequest(Long groupId, Long memberId, int amount) {
        this.groupId = groupId;
        this.memberId = memberId;
        this.amount = amount;
    }

    public Long getGroupId() {
        return groupId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public int getAmount() {
        return amount;
    }
}
