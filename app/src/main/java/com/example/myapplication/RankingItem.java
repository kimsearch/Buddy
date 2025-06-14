package com.example.myapplication;

//랭킹
public class RankingItem {
    private final String name;
    private final int progress;
    private final int profileResId;

    public RankingItem(String name, int progress, int profileResId) {
        this.name = name;
        this.progress = progress;
        this.profileResId = profileResId;
    }

    public String getName() { return name; }
    public int getProgress() { return progress; }
    public int getProfileResId() { return profileResId; }
}