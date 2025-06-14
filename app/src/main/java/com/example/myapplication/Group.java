package com.example.myapplication;

public class Group {
    private Long id;
    private String name;
    private Long leaderId;
    private String goalType;
    private String category;


    public Group() {}

    public Group(Long id, String name, Long leaderId) {
        this.id = id;
        this.name = name;
        this.leaderId = leaderId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public Long getLeaderId() {
        return leaderId;
    }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public String getGoalType() { return goalType; }
    public String getCategory() { return category; }

    public void setLeaderId(Long leaderId) { this.leaderId = leaderId; }
}