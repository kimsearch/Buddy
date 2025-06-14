package com.example.myapplication;

public class CommentItem {
    private String author;
    private String text;

    public CommentItem(String author, String text) {
        this.author = author;
        this.text = text;
    }

    public String getAuthor() { return author; }
    public String getText() { return text; }
}
