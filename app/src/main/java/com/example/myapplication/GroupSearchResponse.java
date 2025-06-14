package com.example.myapplication;

public class GroupSearchResponse {
    private Long id;
    private String name;
    private String category;
    private String startDate;
    private String description;
    private int memberCount;
    private String profileImageUrl; // 이미지 URL도 받을 경우
    private String leaderNickname;
    private boolean joined;

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getMemberCount() { return memberCount; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public boolean isJoined() { return joined; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public String getCategory() {return category;}
    public String getDescription() {return description; }
    public String getStartDate() { return startDate; }
    public String getLeaderNickname() { return leaderNickname; }
    public void setJoined(boolean joined) { this.joined = joined; }

    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}
