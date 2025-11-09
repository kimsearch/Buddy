package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class CommentItem {
    @SerializedName("memberNickname")
    private String author;
    @SerializedName("content")
    private String text;
    private Long id;

    public CommentItem(String author, String text) {
        this.id = null; // 또는 UUID.randomUUID() 등
        this.author = author;
        this.text = text;
    }


    public CommentItem(Long id, String author, String text) {
        this.id = id;
        this.author = author;
        this.text = text;
    }

    public Long getId() { return id; }
    public String getAuthor() { return author; }
    public String getText() { return text; }
}
