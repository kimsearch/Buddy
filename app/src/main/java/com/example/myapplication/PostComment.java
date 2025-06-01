package com.example.myapplication;

public class PostComment {
    private Long id;
    private String content;
    private String createdAt;
    private String memberNickname;

    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
    public String getMemberNickname() { return memberNickname; }
    public static class Member {
        private Long id;
        private String nickname;

        public Long getId() { return id; }
        public String getNickname() { return nickname; }
    }
}
