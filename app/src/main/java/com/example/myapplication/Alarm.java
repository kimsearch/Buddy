package com.example.myapplication;

public class Alarm {
    private Long id;
    private Long memberId;
    private String type;
    private Long targetId;
    private String message;
    private boolean isRead;
    private String createdAt;
    private Long groupId;
    private Long requesterId;

    public boolean isJoinRequest() {
        return "join_request".equals(type);
    }

    public Long getId() { return id; }
    public Long getMemberId() { return memberId; }
    public String getType() { return type; }

    public Long getGroupId() { return groupId; }
    public Long getRequesterId() { return requesterId; }
    public Long getTargetId() { return targetId; }
    public String getMessage() { return message; }
    public boolean isRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }
}
