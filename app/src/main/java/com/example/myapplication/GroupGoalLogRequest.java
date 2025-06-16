package com.example.myapplication;

public class GroupGoalLogRequest {
    private Long groupId;
    private Long memberId;
    private int todaySteps;

    // 생성자
    public GroupGoalLogRequest(Long groupId, Long memberId, int todaySteps) {
        this.groupId = groupId;
        this.memberId = memberId;
        this.todaySteps = todaySteps;
    }

    // Getter/Setter
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public int getTodaySteps() { return todaySteps; }
    public void setTodaySteps(int todaySteps) { this.todaySteps = todaySteps; }
}
