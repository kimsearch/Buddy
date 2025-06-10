package com.example.myapplication;

public class JoinRequestHandleDto {
    private Long groupId;
    private Long requesterId;
    private boolean accepted;

    // 기본 생성자 (필수)
    public JoinRequestHandleDto() {}

    public JoinRequestHandleDto(Long groupId, Long requesterId, boolean accepted) {
        this.groupId = groupId;
        this.requesterId = requesterId;
        this.accepted = accepted;
    }

    // Getter & Setter
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }

    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }
}
