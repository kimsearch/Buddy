package com.example.myapplication.model;

public class GoalTimeRecordDTO {
    private Long groupId;
    private Long userId;
    private Integer goalMinutes;

    public GoalTimeRecordDTO() {
        this.groupId = groupId;
        this.userId = userId;
        this.goalMinutes = goalMinutes;
    }

    public Long getGroupId() { return groupId; }
    public Long getUserId() { return userId; }
    public Integer getGoalMinutes() { return goalMinutes; }

    public void setUserId(Long memberId) {
    }

    public void setGroupId(Long groupId) {
    }

    public void setGoalTime(int goalMinutes) {

    }
}
