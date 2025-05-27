package com.example.myapplication;

public class BuddyGroupDto {
    private String name;
    private String category;
    private int cycleDays;
    private String description;
    private String goalType;

    public BuddyGroupDto(String name, String category, int cycleDays, String description, String goalType) {
        this.name = name;
        this.category = category;
        this.cycleDays = cycleDays;
        this.description = description;
        this.goalType = goalType;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getCycleDays() { return cycleDays; }
    public String getDescription() { return description; }
    public String getGoalType() { return goalType; }

    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setCycleDays(int cycleDays) { this.cycleDays = cycleDays; }
    public void setDescription(String description) { this.description = description; }
    public void setGoalType(String goalType) { this.goalType = goalType; }
}
