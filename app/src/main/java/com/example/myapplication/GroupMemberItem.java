package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class GroupMemberItem {
    private String nickname;
    @SerializedName("leader")
    private boolean isLeader;

    public GroupMemberItem(String nickname, boolean isLeader) {
        this.nickname = nickname;
        this.isLeader = isLeader;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isLeader() {
        return isLeader;
    }
}
