package com.example.myapplication;

public class GoalItem {
    private String title;
    private int completionRate; // 진행률 (0~100)

    public GoalItem(String title, int completionRate) {
        this.title = title;
        this.completionRate = completionRate;
    }

    public String getTitle() {
        return title;
    }

    public int getCompletionRate() {
        return completionRate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompletionRate(int completionRate) {
        this.completionRate = completionRate;
    }
}