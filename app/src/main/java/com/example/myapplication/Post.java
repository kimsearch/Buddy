package com.example.myapplication;

public class Post {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String createdAt;
    private Member member;
    private Group group;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }

    // 내부 클래스 또는 별도 파일로 분리 가능
    public static class Member {
        private Long id;
        private String nickname;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
    }

    public static class Group {
        private Long id;
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
