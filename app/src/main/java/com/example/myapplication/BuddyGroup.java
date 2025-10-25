package com.example.myapplication;

public class BuddyGroup {
    private Long id;
    private String name;
    private String category;
    private String goalType;
    private String description;
    private int cycleDays;
    private Long leaderId;
    private String createdAt;

    // Getter
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getGoalType() { return goalType; }
    public String getDescription() { return description; }
    public int getCycleDays() { return cycleDays; }
    public Long getLeaderId() { return leaderId; }
    public String getCreatedAt() { return createdAt; }

    // Setter
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setGoalType(String goalType) { this.goalType = goalType; }
    public void setDescription(String description) { this.description = description; }
    public void setCycleDays(int cycleDays) { this.cycleDays = cycleDays; }
    public void setLeaderId(Long leaderId) { this.leaderId = leaderId; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
