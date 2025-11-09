package com.example.myapplication.model;

public class TimeLogRecordDTO {

    private Long id;          // 서버에서 자동 생성되는 ID
    private Long userId;      // 사용자 ID
    private Long timeSpent;   // 사용자가 기록한 시간 (분 단위)
    private String createdAt; // 서버에서 내려주는 생성 시각 (예: 2025-10-31T12:30:00)

    // ✅ 기본 생성자 (필수 — Gson이 사용)
    public TimeLogRecordDTO() {}

    // ✅ 생성자 (보낼 때 사용)
    public TimeLogRecordDTO(Long userId, Long timeSpent) {
        this.userId = userId;
        this.timeSpent = timeSpent;
    }

    // ✅ Getter / Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getNickname() {
        return "";
    }

    public Object getRecordDate() {
        return null;
    }

    public void setGroupId(Long groupId) {
    }
}

