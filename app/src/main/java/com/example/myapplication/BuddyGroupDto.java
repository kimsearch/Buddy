package com.example.myapplication;

public class BuddyGroupDto {
    private String name;
    private String category;
    private int cycleDays;
    private String description;
    private String goalType;
    private String startDate; // ✅ 시작일 추가
    private Integer goalValue;

    public BuddyGroupDto(String name, String category, int cycleDays, String description, String goalType, String startDate, int goalValue) {
        this.name = name;
        this.category = category;
        this.cycleDays = cycleDays;
        this.description = description;
        this.goalType = goalType;
        this.startDate = startDate;
        this.goalValue = goalValue;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getCycleDays() { return cycleDays; }
    public String getDescription() { return description; }
    public String getGoalType() { return goalType; }
    public String getStartDate() { return startDate; }

    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setCycleDays(int cycleDays) { this.cycleDays = cycleDays; }
    public void setDescription(String description) { this.description = description; }
    public void setGoalType(String goalType) { this.goalType = goalType; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
}
